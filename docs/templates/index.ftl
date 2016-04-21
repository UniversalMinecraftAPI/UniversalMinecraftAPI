<#assign title="UniversalMinecraftAPI">
<#include "common/header.ftl">

<h1>UniversalMinecraftAPI</h1>

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

<button class="btn btn-primary" type="button" data-toggle="collapse" data-target="#generationInfo" aria-expanded="false"
        aria-controls="generationInfo">
    Show generation info
</button>
<div class="collapse" id="generationInfo">
    <div class="well">
        <p><strong>Generation time: </strong> ${now?datetime?iso("UTC")}</p>
        <strong>Platforms included: </strong>
        <table class="table">
            <thead>
            <tr>
                <td>Platform name</td>
                <td>Platform reported name</td>
                <td>Platform version</td>
                <td>UMA version</td>
            </tr>
            </thead>
            <tbody>
            <#list platforms as platform>
            <tr>
                <td>${platform.name}</td>
                <td>${platform.rawName}</td>
                <td>${platform.version}</td>
                <td>${platform.umaVersion}</td>
            </tr>
            </#list>
            </tbody>
        </table>
    </div>
</div>
<#include "common/footer.ftl">