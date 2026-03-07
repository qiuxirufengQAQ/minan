#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
AI NPC 提示词模板引擎
支持动态变量注入、模板版本管理、使用日志记录
"""

import json
import time
import re
from datetime import datetime
from typing import Dict, Any, Optional, List


class PromptTemplateEngine:
    """提示词模板引擎"""
    
    def __init__(self, db_connection=None):
        """
        初始化模板引擎
        
        Args:
            db_connection: 数据库连接（可选，不使用数据库时可用字典配置）
        """
        self.db = db_connection
        self.template_cache = {}
        self.cache_ttl = 300  # 缓存 5 分钟
    
    # ==================== 模板管理 ====================
    
    def get_template(self, template_type: str, version: Optional[int] = None) -> Optional[Dict]:
        """
        获取模板配置
        
        Args:
            template_type: 模板类型（role_play/coach/evaluation）
            version: 版本号（可选，默认取最新版本）
        
        Returns:
            模板配置字典，包含 id, content, variables, version
        """
        # 检查缓存
        cache_key = f"{template_type}:{version or 'latest'}"
        if cache_key in self.template_cache:
            cached = self.template_cache[cache_key]
            if time.time() - cached["timestamp"] < self.cache_ttl:
                return cached["data"]
        
        if self.db:
            # 从数据库获取
            template = self._get_template_from_db(template_type, version)
        else:
            # 从默认配置获取
            template = self._get_template_from_defaults(template_type)
        
        if template:
            self.template_cache[cache_key] = {
                "data": template,
                "timestamp": time.time()
            }
            return template
        
        return None
    
    def _get_template_from_db(self, template_type: str, version: Optional[int]) -> Optional[Dict]:
        """从数据库获取模板"""
        try:
            cursor = self.db.cursor(dictionary=True)
            
            if version:
                query = """
                    SELECT id, template_content, variable_mapping, version
                    FROM prompt_template
                    WHERE template_type = %s AND version = %s AND is_active = 1
                    LIMIT 1
                """
                cursor.execute(query, (template_type, version))
            else:
                query = """
                    SELECT id, template_content, variable_mapping, version
                    FROM prompt_template
                    WHERE template_type = %s AND is_active = 1
                    ORDER BY version DESC
                    LIMIT 1
                """
                cursor.execute(query, (template_type,))
            
            result = cursor.fetchone()
            cursor.close()
            
            if result:
                return {
                    "id": result["id"],
                    "content": result["template_content"],
                    "variables": json.loads(result["variable_mapping"]),
                    "version": result["version"]
                }
            
            return None
            
        except Exception as e:
            print(f"[ERROR] 获取模板失败：{e}")
            return None
    
    def _get_template_from_defaults(self, template_type: str) -> Optional[Dict]:
        """从默认配置获取模板（无数据库时使用）"""
        from .templates import DEFAULT_TEMPLATES
        return DEFAULT_TEMPLATES.get(template_type)
    
    def clear_cache(self, template_type: Optional[str] = None):
        """
        清除缓存
        
        Args:
            template_type: 指定类型的缓存（可选，不传则清除所有）
        """
        if template_type:
            keys_to_delete = [k for k in self.template_cache if k.startswith(template_type)]
            for key in keys_to_delete:
                del self.template_cache[key]
        else:
            self.template_cache.clear()
    
    # ==================== 变量处理 ====================
    
    def fetch_variable_value(self, var_config: Dict, context: Dict) -> Any:
        """
        获取变量值
        
        Args:
            var_config: 变量配置
            context: 上下文数据
        
        Returns:
            变量值
        """
        source = var_config.get("source", "dynamic")
        field = var_config.get("field")
        default = var_config.get("default")
        required = var_config.get("required", False)
        
        # 1. 动态变量（直接从 context 取）
        if source == "dynamic":
            value = context.get(field, default)
        
        # 2. 数据库变量（从 NPC/Scene/User 表取）
        elif source in ["npc_character", "scene", "user"]:
            data = context.get(source, {})
            value = data.get(field)
            
            # 如果值为空，使用默认值
            if value is None:
                value = default
        
        # 3. 计算变量（通过函数计算）
        elif source == "computed":
            compute_func = var_config.get("compute")
            if compute_func and callable(compute_func):
                value = compute_func(context)
            else:
                value = default
        
        else:
            value = default
        
        # 必填字段检查
        if required and value is None:
            raise ValueError(f"必填变量 '{field}' 缺失值")
        
        return value
    
    def _format_history(self, history: List[Dict], max_rounds: int = 3) -> str:
        """
        格式化对话历史
        
        Args:
            history: 对话历史列表
            max_rounds: 最大轮数
        
        Returns:
            格式化后的历史字符串
        """
        recent = history[-max_rounds:] if len(history) > max_rounds else history
        lines = []
        for item in recent:
            user_input = item.get("user_input", "")
            npc_response = item.get("npc_response", "")
            lines.append(f"用户：{user_input}")
            lines.append(f"NPC: {npc_response}")
        return "\n".join(lines)
    
    def _process_conditionals(self, template_content: str, context: Dict) -> str:
        """
        处理条件渲染
        
        支持语法：
        {#if variable}...{/if}
        {#if variable}...{#else}...{/if}
        
        Args:
            template_content: 模板内容
            context: 上下文数据
        
        Returns:
            处理后的模板内容
        """
        # 处理 {#if var}...{/if}
        pattern = r'\{#if\s+(\w+)\}(.*?)(?:\{#else\}(.*?))?\{/if\}'
        
        def replace_conditional(match):
            var_name = match.group(1)
            if_content = match.group(2)
            else_content = match.group(3) or ""
            
            # 获取变量值
            value = context.get(var_name)
            
            # 判断真假
            if value:
                return if_content
            else:
                return else_content
        
        return re.sub(pattern, replace_conditional, template_content, flags=re.DOTALL)
    
    def _process_transforms(self, value: Any, var_config: Dict) -> Any:
        """
        处理变量转换
        
        Args:
            value: 原始值
            var_config: 变量配置
        
        Returns:
            转换后的值
        """
        transform = var_config.get("transform")
        
        if not transform:
            return value
        
        # 映射转换
        if transform == "map":
            transform_config = var_config.get("transform_config", {})
            return transform_config.get(str(value), value)
        
        # 字符串转换
        elif transform == "string":
            return str(value)
        
        # 数字转换
        elif transform == "int":
            return int(value) if value else 0
        
        # 函数转换
        elif transform == "function":
            func = var_config.get("transform_func")
            if func and callable(func):
                return func(value)
        
        return value
    
    # ==================== 提示词构建 ====================
    
    def build_prompt(self, template_type: str, context: Dict) -> str:
        """
        构建提示词
        
        Args:
            template_type: 模板类型
            context: 上下文数据
        
        Returns:
            构建好的提示词
        """
        # 1. 获取模板
        template = self.get_template(template_type)
        if not template:
            raise ValueError(f"模板 '{template_type}' 不存在")
        
        # 2. 处理条件渲染
        content = self._process_conditionals(template["content"], context)
        
        # 3. 解析变量
        variables = {}
        for var_name, var_config in template["variables"].items():
            try:
                value = self.fetch_variable_value(var_config, context)
                
                # 特殊处理：对话历史
                if var_name == "conversation_history":
                    history = context.get("conversation_history", [])
                    max_rounds = var_config.get("max_rounds", 3)
                    value = self._format_history(history, max_rounds)
                
                # 处理变量转换
                value = self._process_transforms(value, var_config)
                
                variables[var_name] = value
                
            except Exception as e:
                print(f"[WARNING] 变量 '{var_name}' 处理失败：{e}")
                variables[var_name] = var_config.get("default", "")
        
        # 4. 渲染模板
        try:
            prompt = content.format(**variables)
        except KeyError as e:
            print(f"[ERROR] 模板变量缺失：{e}")
            raise
        
        return prompt
    
    def build_prompt_batch(self, template_type: str, contexts: List[Dict]) -> List[str]:
        """
        批量构建提示词
        
        Args:
            template_type: 模板类型
            contexts: 上下文列表
        
        Returns:
            提示词列表
        """
        return [self.build_prompt(template_type, context) for context in contexts]
    
    # ==================== 使用日志 ====================
    
    def log_usage(self, template_id: int, **kwargs):
        """
        记录模板使用日志
        
        Args:
            template_id: 模板 ID
            **kwargs: 其他参数（npc_id, scene_id, user_id, tokens_used, response_time_ms）
        """
        if not self.db:
            return
        
        try:
            cursor = self.db.cursor()
            query = """
                INSERT INTO prompt_template_usage 
                (template_id, npc_id, scene_id, user_id, tokens_used, response_time_ms)
                VALUES (%s, %s, %s, %s, %s, %s)
            """
            cursor.execute(query, (
                template_id,
                kwargs.get("npc_id"),
                kwargs.get("scene_id"),
                kwargs.get("user_id"),
                kwargs.get("tokens_used", 0),
                kwargs.get("response_time_ms", 0)
            ))
            self.db.commit()
            cursor.close()
            
        except Exception as e:
            print(f"[ERROR] 记录使用日志失败：{e}")
    
    def get_usage_stats(self, template_id: int, days: int = 7) -> Dict:
        """
        获取模板使用统计
        
        Args:
            template_id: 模板 ID
            days: 统计天数
        
        Returns:
            统计数据
        """
        if not self.db:
            return {}
        
        try:
            cursor = self.db.cursor(dictionary=True)
            query = """
                SELECT 
                    COUNT(*) as total_calls,
                    AVG(tokens_used) as avg_tokens,
                    AVG(response_time_ms) as avg_response_time,
                    MIN(created_at) as first_used,
                    MAX(created_at) as last_used
                FROM prompt_template_usage
                WHERE template_id = %s 
                AND created_at >= DATE_SUB(NOW(), INTERVAL %s DAY)
            """
            cursor.execute(query, (template_id, days))
            result = cursor.fetchone()
            cursor.close()
            
            return result or {}
            
        except Exception as e:
            print(f"[ERROR] 获取使用统计失败：{e}")
            return {}
    
    # ==================== 版本管理 ====================
    
    def switch_version(self, template_type: str, version: int):
        """
        切换模板版本
        
        Args:
            template_type: 模板类型
            version: 目标版本
        """
        if not self.db:
            return
        
        try:
            cursor = self.db.cursor()
            
            # 先禁用所有版本
            cursor.execute("""
                UPDATE prompt_template 
                SET is_active = 0 
                WHERE template_type = %s
            """, (template_type,))
            
            # 启用指定版本
            cursor.execute("""
                UPDATE prompt_template 
                SET is_active = 1 
                WHERE template_type = %s AND version = %s
            """, (template_type, version))
            
            self.db.commit()
            cursor.close()
            
            # 清除缓存
            self.clear_cache(template_type)
            
        except Exception as e:
            print(f"[ERROR] 切换版本失败：{e}")
    
    def create_version(self, template_type: str, content: str, variables: Dict, 
                      description: str = "") -> int:
        """
        创建新版本模板
        
        Args:
            template_type: 模板类型
            content: 模板内容
            variables: 变量映射配置
            description: 版本描述
        
        Returns:
            新版本号
        """
        if not self.db:
            raise ValueError("需要数据库连接")
        
        try:
            cursor = self.db.cursor()
            
            # 获取最新版本号
            cursor.execute("""
                SELECT MAX(version) FROM prompt_template 
                WHERE template_type = %s
            """, (template_type,))
            result = cursor.fetchone()
            new_version = (result[0] or 0) + 1
            
            # 插入新版本
            query = """
                INSERT INTO prompt_template 
                (template_type, template_content, variable_mapping, version, description)
                VALUES (%s, %s, %s, %s, %s)
            """
            cursor.execute(query, (
                template_type,
                content,
                json.dumps(variables, ensure_ascii=False),
                new_version,
                description
            ))
            
            self.db.commit()
            cursor.close()
            
            return new_version
            
        except Exception as e:
            print(f"[ERROR] 创建版本失败：{e}")
            raise
    
    # ==================== 测试工具 ====================
    
    def test_template(self, template_type: str, test_context: Dict) -> Dict:
        """
        测试模板
        
        Args:
            template_type: 模板类型
            test_context: 测试上下文
        
        Returns:
            测试结果
        """
        start_time = time.time()
        
        try:
            # 构建提示词
            prompt = self.build_prompt(template_type, test_context)
            
            # 统计信息
            tokens_estimated = len(prompt) // 4  # 粗略估算
            
            return {
                "success": True,
                "prompt": prompt,
                "tokens_estimated": tokens_estimated,
                "build_time_ms": int((time.time() - start_time) * 1000),
                "variables_used": len(test_context)
            }
            
        except Exception as e:
            return {
                "success": False,
                "error": str(e),
                "build_time_ms": int((time.time() - start_time) * 1000)
            }


# ==================== 快捷函数 ====================

def create_engine(db_connection=None) -> PromptTemplateEngine:
    """创建模板引擎实例"""
    return PromptTemplateEngine(db_connection)


def quick_build(template_type: str, context: Dict, db_connection=None) -> str:
    """快速构建提示词"""
    engine = create_engine(db_connection)
    return engine.build_prompt(template_type, context)
