<#assign title="UniversalMinecraftAPI">
<#include "common/header.ftl">

<div class="row">
    <div class="col-md-7">
        <h1>UniversalMinecraftAPI</h1>

    ${introduction}

        <h2>Table of contents</h2>
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
    </div>
    <div class="col-md-4 col-md-offset-1">
        <div class="sidebar">
            <h3>Links</h3>
            <p><a href="https://github.com/UniversalMinecraftAPI/UniversalMinecraftAPI/releases"
                  class="btn btn-success btn-lg btn-block">Downloads</a></p>
            <div class="list-group">
                <a class="list-group-item" href="https://github.com/UniversalMinecraftAPI/UniversalMinecraftAPI/issues"
                   target="_blank">Issues</a>
                <a class="list-group-item" href="https://github.com/UniversalMinecraftAPI/UniversalMinecraftAPI"
                   target="_blank">Source code</a>
                <a class="list-group-item" href="http://ci.koenv.com/job/UniversalMinecraftAPI/" target="_blank">Jenkins
                    builds</a>
            </div>
        </div>
    </div>
</div>

<p>
    <button class="btn btn-default btn-sm" type="button" data-toggle="collapse" data-target="#generationInfo"
            aria-expanded="false"
            aria-controls="generationInfo">
        Show generation info
    </button>
</p>
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