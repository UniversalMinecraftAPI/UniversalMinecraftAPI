<!doctype>
<html>
<head>
    <#if basedir != "">
        <base href="${basedir}">
    </#if>
    <title>${title} - UniversalMinecraftAPI</title>
    <link href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css" rel="stylesheet"/>
    <link rel="stylesheet" href="//cdnjs.cloudflare.com/ajax/libs/highlight.js/9.3.0/styles/vs.min.css">
    <link rel="stylesheet" href="assets/css/style.css">
</head>
<body>
<div class="container">

<#macro type type><#if type.hasOwnDocumentation()><a href="classes/${type.documentationName}.html">${type.name}</a><#else>${type.name}</#if></#macro>