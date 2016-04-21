<#assign title="JSONAPI">
<#include "common/header.ftl">

<h1>JSONAPI</h1>

<ol>
<#list pages as page>
    <li><a href="${page.link}">${page.title}</a></li>
</#list>
</ol>

<h2>Reference</h2>

<h3>Namespaces</h3>
<ul>
<#list namespaces as namespace>
    <li><a href="namespaces/${namespace}.html">${namespace}</a></li>
</#list>
</ul>

<h3>Objects</h3>
<ul>
<#list classes as class>
    <li><a href="classes/${class}.html">${class}</a></li>
</#list>
</ul>

<h3>Streams</h3>
<ul>
<#list streams as stream>
    <li><a href="streams/${stream}.html">${stream}</a></li>
</#list>
</ul>
<#include "common/footer.ftl">