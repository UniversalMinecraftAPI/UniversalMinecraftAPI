<#assign title="Namespace " + namespace>
<#include "common/header.ftl">

<h1>Namespace ${namespace}</h1>
<#if description??>
<p class="lead">${description}</p>
</#if>

<div class="row">
<#list methods as method>
    <div class="col-md-12" id="${method.name}">
        <h2>${method.declarationWithoutNamespace}</h2>
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
                <tr>
                    <td width="30%">Permission</td>
                    <td width="70%"><code>${method.permission}</code></td>
                </tr>
                <#if method.arguments?size != 0>
                    <tr>
                        <td width="30%">Arguments</td>
                        <td width="70%">
                            <#list method.arguments as argument>
                                <#if argument.optional><span class="label label-info">Optional</span></#if>
                                <strong><@type type=argument.type/></strong> ${argument.name}:
                            ${method.argumentDescriptions[argument.name]!''}<br/>
                            </#list>
                        </td>
                    </tr>
                </#if>
                <#if method.returns.name != "void">
                    <tr>
                        <td width="30%">Returns</td>
                        <td width="70%"><strong><@type type=method.returns/></strong>: ${method.returnDescription}</td>
                    </tr>
                </#if>
            </table>
        </div>

        <#if method.example != "">
            <h3>Sample</h3>
            <code>${method.example}</code>

            <br/><br/>
            <button class="btn btn-primary" type="button" data-toggle="collapse" data-target="#${method.name}Example"
                    aria-expanded="false" aria-controls="${method.name}Example">
                Click to view sample request
            </button>
            <div class="collapse" id="${method.name}Example">
                <ul class="nav nav-tabs tabList" role="tablist">
                    <li role="presentation" class="active"><a href="#${method.name}HTTP"
                                                              aria-controls="${method.name}HTTP" role="tab"
                                                              data-toggle="tab">HTTP</a></li>
                    <li role="presentation"><a href="#${method.name}Curl" aria-controls="profile" role="tab"
                                               data-toggle="${method.name}Curl">Curl</a>
                    </li>
                    <li role="presentation"><a href="#${method.name}HTTPie" aria-controls="${method.name}HTTPie"
                                               role="tab" data-toggle="tab">HTTPie</a>
                    </li>
                </ul>
                <div class="tab-content">
                    <div role="tabpanel" class="tab-pane active" id="${method.name}HTTP">
                        <pre><code class="language-http">POST /api/v1/call HTTP/1.1
Host: localhost:20059
Content-Type: application/json
Authorization: Basic YWRtaW46Y2hhbmdlbWU=

{
    "expression": ${method.jsonExample}
}</code></pre>
                    </div>
                    <div role="tabpanel" class="tab-pane" id="${method.name}Curl">
                        <pre><code class="language-bash">curl -X POST \
-H "Content-Type: application/json" \
-H "Authorization: Basic YWRtaW46Y2hhbmdlbWU=" -d '{
    "expression": ${method.jsonExample}
}' "http://localhost:20059/api/v1/call"</code></pre>
                    </div>
                    <div role="tabpanel" class="tab-pane" id="${method.name}HTTPie">
                        <pre><code class="language-bash">http -a admin:changeme POST http://localhost:20059/api/v1/call \
content-type:application/json expression=${method.jsonExample}</code></pre>
                    </div>
                </div>
            </div>
        </#if>
    </div>
</#list>
</div>
<#include "common/footer.ftl">