// Service 接口模板
// 使用说明：补建 Service 中间层时，按此模板创建 Service 接口
// 将 {ServiceName}、{ReturnType}、{MethodName}、{ParamType}、{paramName} 替换为实际值

package {basePackage}.service;

/**
 * {ServiceName} 服务接口
 */
public interface I{ServiceName}Service {

    /**
     * {方法说明}
     *
     * @param {paramName} {参数说明}
     * @return {返回值说明}
     */
    {ReturnType} {methodName}({ParamType} {paramName});
}
