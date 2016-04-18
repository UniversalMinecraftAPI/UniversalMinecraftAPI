<#assign title="Stream " + stream>
<#include "common/header.ftl">

<h1>Stream ${stream}</h1>
<#if description??>
<p class="lead">${description}</p>
</#if>
<#if returns??>
Returns: <@type type=returns/>
</#if>

<#include "common/footer.ftl">