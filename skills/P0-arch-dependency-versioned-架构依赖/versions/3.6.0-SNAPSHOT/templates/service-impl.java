// ServiceImpl 实现类模板
// 使用说明：补建 Service 中间层时，按此模板创建 Service 实现类
// 将 {ServiceName}、{ReturnType}、{MethodName}、{DaoName} 等替换为实际值

package {basePackage}.service.impl;

import {basePackage}.service.I{ServiceName}Service;
import {basePackage}.dao.{DaoName};
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * {ServiceName} 服务实现类
 */
@Service
public class {ServiceName}ServiceImpl implements I{ServiceName}Service {

    @Autowired
    private {DaoName} {daoFieldName};

    @Override
    public {ReturnType} {methodName}({ParamType} {paramName}) {
        // 从 Controller 中迁移过来的 DAO 调用逻辑
        return {daoFieldName}.{daoMethod}({paramName});
    }
}
