<#assign title="JSONAPI">
<#include "common/header.ftl">

<h1>JSONAPI</h1>

<h2>Namespaces</h2>
<ul>
<#list namespaces as namespace>
    <li><a href="/namespaces/${namespace}.html">${namespace}</a></li>
</#list>
</ul>

<h2>Objects</h2>
<ul>
<#list classes as class>
    <li><a href="/classes/${class}.html">${class}</a></li>
</#list>
</ul>

<h2>Streams</h2>
<ul>
<#list streams as stream>
    <li><a href="/streams/${stream}.html">${stream}</a></li>
</#list>
</ul>
<#include "common/footer.ftl">