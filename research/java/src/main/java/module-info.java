open module lws.sc {
    requires javax.servlet.api;
    requires spring.core;
    requires spring.boot;
    requires spring.web;
    requires org.slf4j;
    requires spring.beans;
    requires spring.context;
    requires spring.boot.autoconfigure;
    requires spring.boot.starter.web;
    requires com.google.gson;
    requires java.annotation;
    requires flexmark;
    requires flexmark.util.ast;
    requires flexmark.util.data;
    requires flexmark.util.misc;
    requires flexmark.util.sequence;
    requires flexmark.util.builder;
    requires flexmark.ext.gitlab;
    requires flexmark.ext.tables;
    requires flexmark.ext.gfm.strikethrough;
    requires flexmark.ext.toc;
}
