##部门
1. id ==> code code 从16位变为128位 【done】
2. 增加启用\禁用
3. department_type_id 0:部门 1:分部
4. 三级结构缺失

##operator

###创建者
1. 创建人 【done】
2. 创建人经理 【done】
3. 创建人本分部 [done]
4. 创建人本部门 【done】
5. 创建人上级部门【done】
6. 分部 [done]
7. 部门 [done]
8. 角色 [done]
9. 人力资源 [done]
10. 所有人 [done]

###人力资源字段
1. 人力资源字段本人
2. 人力资源字段经理
3. 人力资源字段本分部
4. 人力资源字段本部门
5. 人力资源字段上级部门
6. 分部
7. 部门
8. 角色
9. 角色人员

###节点操作者
1. 节点操作者本人
2. 节点操作者经理

##操作条件
###创建人
1. 创建人 【done】
2. 创建人经理 【done】
3. 创建部门 【done】
4. 创建人本分部 【done】

###表单条件
1. 数字 [lt\lte\gt\gte] [done]
2. 日期 [lt\lte\gt\gte] [done]
3. 时间 [lt\lte\gt\gte] [done]
3. 字符串 [done] eq\neq\contain\no-contain
4. 外链 [in\not-in] [done]


##会签操作
1. 表明一个节点