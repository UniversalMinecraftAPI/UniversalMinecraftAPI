<!doctype>
<html>
<head>
    <title>${title} - JSONAPI</title>
    <link href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css" rel="stylesheet"/>
</head>
<body>
<div class="container">

<#macro type type>
    <#if type.hasOwnDocumentation()>
        <a href="/classes/${type.documentationName}.html">${type.name}</a>
    <#else>
        ${type.name}
    </#if>
</#macro>