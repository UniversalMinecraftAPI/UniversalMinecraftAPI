<#assign title="Class " + class>
<#include "common/header.ftl">

<h1>Class ${class}</h1>
<#if description??>
<p class="lead">${description}</p>
</#if>

<#if model??>
    <h2>Model</h2>
    <#assign keys = model?keys>
    <#list keys as field>
    <strong><@type type=model[field].type/></strong> ${field}: ${model[field].description}<br/>
    </#list>
</#if>

<#if methods??>
<div class="row">
    <#list methods as method>
        <div class="col-md-12">
            <h2>${method.declarationWithoutOperatesOn}</h2>
            <p class="lead">${method.description}</p>

            <#if !method.availableOnAllPlatforms>
                <div class="alert alert-warning" role="alert">
                    <strong>Warning!</strong> This method is not available on all platforms. It is only available on the
                    following platforms:
                    <ul>
                        <#list method.platforms as platform>
                            <li>${platform.name}</li>
                        </#list>
                    </ul>
                </div>
            </#if>

            <div class="table-responsive">
                <table class="table">
                    <#if method.arguments?size != 0>
                        <tr>
                            <td width="30%">Arguments</td>
                            <td width="70%">
                                <#list method.arguments as argument>
                                    <#if argument.optional><span class="label label-info">Optional</span></#if>
                                    <strong><@type type=argument.type/></strong> ${argument.name}: ${method.argumentDescriptions[argument.name]!''}<br/>
                                </#list>
                            </td>
                        </tr>
                    </#if>
                    <#if method.returns != "void">
                        <tr>
                            <td width="30%">Returns</td>
                            <td width="70%"><strong><@type type=method.returns/></strong>: ${method.returnDescription}
                            </td>
                        </tr>
                    </#if>
                </table>
            </div>
        </div>
    </#list>
</div>
</#if>
<#include "common/footer.ftl">