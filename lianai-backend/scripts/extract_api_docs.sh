#!/bin/bash

# 提取所有 Controller 的接口信息
# 输出格式：Controller 名 | 基础路径 | HTTP 方法 | 完整路径 | 方法名 | 描述

CONTROLLER_DIR="/root/.copaw/data/minan/minan-backend/src/main/java/com/minan/game/controller"

echo "# Minan Backend API 接口文档"
echo ""
echo "生成时间：$(date '+%Y-%m-%d %H:%M:%S')"
echo "Context Path: /api"
echo ""
echo "---"
echo ""

for file in $(find $CONTROLLER_DIR -name "*Controller.java" -type f | sort); do
    filename=$(basename "$file")
    controller_name=${filename%.java}
    
    # 提取 @RequestMapping 路径
    base_path=$(grep -o '@RequestMapping("[^"]*")' "$file" | head -1 | cut -d'"' -f2)
    
    # 如果没有 @RequestMapping，检查 @RestController
    if [ -z "$base_path" ]; then
        base_path="/"
    fi
    
    echo "## $controller_name"
    echo ""
    echo "**基础路径**: \`/api$base_path\`"
    echo ""
    
    # 提取所有接口
    echo "| 方法 | 路径 | 完整 URL | 说明 |"
    echo "|------|------|----------|------|"
    
    # 读取文件内容，提取接口信息
    while IFS= read -r line; do
        # 检查是否有映射注解
        if echo "$line" | grep -qE '@(GetMapping|PostMapping|PutMapping|DeleteMapping|PatchMapping)'; then
            method=$(echo "$line" | grep -oE '@(GetMapping|PostMapping|PutMapping|DeleteMapping|PatchMapping)' | tr -d '@')
            path=$(echo "$line" | grep -oE '"[^"]*"' | head -1 | tr -d '"')
            
            # 转换为 HTTP 方法
            http_method="GET"
            case $method in
                GetMapping) http_method="GET" ;;
                PostMapping) http_method="POST" ;;
                PutMapping) http_method="PUT" ;;
                DeleteMapping) http_method="DELETE" ;;
                PatchMapping) http_method="PATCH" ;;
            esac
            
            # 构建完整路径
            if [ "$path" = "/" ] || [ -z "$path" ]; then
                full_path="/api$base_path"
            elif [[ "$base_path" = "/" ]]; then
                full_path="/api$path"
            else
                full_path="/api$base_path$path"
            fi
            
            # 提取方法名和描述（下一行或同一行）
            method_info=$(grep -A1 "$line" "$file" | tail -1 | grep -oE 'public [^(]+' | head -1)
            method_name=$(echo "$method_info" | grep -oE '[a-zA-Z]+\(' | tr -d '(' | head -1)
            
            # 尝试提取 @ApiOperation 描述
            description=$(grep -B5 "$line" "$file" | grep -oE '@ApiOperation\("[^"]*"\)' | head -1 | cut -d'"' -f2)
            if [ -z "$description" ]; then
                description="-"
            fi
            
            echo "| $http_method | \`$path\` | \`$full_path\` | $description |"
        fi
    done < "$file"
    
    echo ""
done
