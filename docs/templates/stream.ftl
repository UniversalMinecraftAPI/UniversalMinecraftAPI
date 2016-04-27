<#assign title="Stream " + stream>
<#include "common/header.ftl">

<h1>Stream ${stream}</h1>
<#if description??>
<p class="lead">${description}</p>
</#if>
<p><strong>Permission</strong>: <code>streams.${stream}</code></p>
<#if returns??>
<p><strong>Returns</strong>: <@type type=returns/></p>
</#if>
<#if !availableOnAllPlatforms>
<div class="alert alert-warning" role="alert">
    <strong>Warning!</strong> This stream is not available on all platforms. It is only available on the
    following platforms:
    <ul>
        <#list platforms as platform>
            <li>${platform.name}</li>
        </#list>
    </ul>
</div>
</#if>

<#include "common/footer.ftl">