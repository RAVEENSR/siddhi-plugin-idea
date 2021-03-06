/*
 *  Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.wso2.siddhi.plugins.idea.completion;

import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.PrioritizedLookupElement;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.template.Template;
import com.intellij.codeInsight.template.TemplateManager;
import com.intellij.codeInsight.template.impl.TemplateSettings;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorModificationUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.wso2.siddhi.plugins.idea.SiddhiIcons;
import org.wso2.siddhi.plugins.idea.psi.StreamDefinitionNode;
import org.wso2.siddhi.plugins.idea.psi.TableDefinitionNode;
import org.wso2.siddhi.plugins.idea.psi.WindowDefinitionNode;

import java.util.LinkedList;
import java.util.List;

/**
 * Suggests parenthesis.
 */
public class SiddhiCompletionUtils {

    private static final int VARIABLE_PRIORITY = 20;
    private static final int VALUE_TYPES_PRIORITY = VARIABLE_PRIORITY - 1;
    private static final int KEYWORDS_PRIORITY = VALUE_TYPES_PRIORITY - 2;


    //Initial Definition Types
    private static final LookupElementBuilder DEFINE;
    private static final LookupElementBuilder PARTITION;
    private static final LookupElementBuilder FROM;
    private static final LookupElementBuilder AT_SYMBOL;
    private static final LookupElementBuilder HASH_SYMBOL;
    private static final LookupElementBuilder COMMA_SYMBOL;
    private static final LookupElementBuilder SEMI_COLON_SYMBOL;
    private static final LookupElementBuilder QUERY_SNIIP;
    private static final LookupElementBuilder QUERY_PATTERN_SNIP;
    private static final LookupElementBuilder QUERY_JOIN_SNIIP;
    private static final LookupElementBuilder QUERY_WINDOW_FILTER_SNIIP;
    private static final LookupElementBuilder QUERY_FILTER_SNIIP;
    private static final LookupElementBuilder QUERY_WINDOW_SNIIP;
    private static final LookupElementBuilder ANNOTATION_SINK;
    private static final LookupElementBuilder ANNOTATION_SOURCE;
    private static final LookupElementBuilder PARTITION_SNIIP;
    private static final LookupElementBuilder ANNOTATION_CONFIG_SNIIP;
    private static final LookupElementBuilder DEFINITION_STREAM;
    private static final LookupElementBuilder ANNOTATION_EXPORTSTREAM;
    private static final LookupElementBuilder ANNOTATION_IMPORTSTREAM;
    private static final LookupElementBuilder ANNOTATION_APPTRACE;
    private static final LookupElementBuilder ANNOTATION_APPSTATS;
    private static final LookupElementBuilder ANNOTATION_APPDESC;
    private static final LookupElementBuilder ANNOTATION_APPNAME;
    private static final LookupElementBuilder ANNOTATION_PRIMARYKEY;
    private static final LookupElementBuilder ANNOTATION_INDEX;
    private static final LookupElementBuilder DEFINE_FUNCTION;
    private static final LookupElementBuilder DEFINE_TRIGGER;
    private static final LookupElementBuilder DEFINE_WINDOW;
    private static final LookupElementBuilder DEFINE_TABLE;
    private static final LookupElementBuilder DEFINE_AGGREGATION;
    private static final LookupElementBuilder ANNOTATION_INFO;

    //annotations without @ sign(used to display suggestions after @)
    private static final LookupElementBuilder ANNOTATION_SINK2;
    private static final LookupElementBuilder ANNOTATION_SOURCE2;
    private static final LookupElementBuilder ANNOTATION_CONFIG_SNIIP2;
    private static final LookupElementBuilder ANNOTATION_EXPORTSTREAM2;
    private static final LookupElementBuilder ANNOTATION_IMPORTSTREAM2;
    private static final LookupElementBuilder ANNOTATION_APPTRACE2;
    private static final LookupElementBuilder ANNOTATION_APPSTATS2;
    private static final LookupElementBuilder ANNOTATION_APPDESC2;
    private static final LookupElementBuilder ANNOTATION_APPNAME2;
    private static final LookupElementBuilder ANNOTATION_PRIMARYKEY2;
    private static final LookupElementBuilder ANNOTATION_INDEX2;
    private static final LookupElementBuilder ANNOTATION_INFO2;
    private static final LookupElementBuilder ANNOTATION_MAP;
    private static final LookupElementBuilder ANNOTATION_ATTRIBUTES;
    private static final LookupElementBuilder ANNOTATION_PAYLOARD;

    //Define Types
    private static final LookupElementBuilder STREAM;
    private static final LookupElementBuilder TABLE;
    private static final LookupElementBuilder TRIGGER;
    private static final LookupElementBuilder FUNCTION;
    private static final LookupElementBuilder WINDOW;

    //Logical Operators
    private static final LookupElementBuilder AND;
    private static final LookupElementBuilder OR;
    private static final LookupElementBuilder NOT;
    private static final LookupElementBuilder IN;
    private static final LookupElementBuilder IS;
    private static final LookupElementBuilder IS_NULL;

    //Output Event Types
    private static final LookupElementBuilder CURRENT_EVENTS;
    private static final LookupElementBuilder ALL_EVENTS;
    private static final LookupElementBuilder EXPIRED_EVENTS;

    //Time Value Types
    private static final LookupElementBuilder YEARS;
    private static final LookupElementBuilder MONTHS;
    private static final LookupElementBuilder WEEKS;
    private static final LookupElementBuilder DAYS;
    private static final LookupElementBuilder HOURS;
    private static final LookupElementBuilder MINUTES;
    private static final LookupElementBuilder SECONDS;
    private static final LookupElementBuilder MILLISECONDS;

    //Data Types
    private static final LookupElementBuilder STRING;
    private static final LookupElementBuilder INT;
    private static final LookupElementBuilder LONG;
    private static final LookupElementBuilder FLOAT;
    private static final LookupElementBuilder DOUBLE;
    private static final LookupElementBuilder BOOL;
    private static final LookupElementBuilder OBJECT;

    private static final LookupElementBuilder EVERY;
    private static final LookupElementBuilder AT;

    //Window Processor Code Snippets without window keyword
    private static final LookupElementBuilder LENGTH;
    private static final LookupElementBuilder LENGTHBATCH;
    private static final LookupElementBuilder SORT;
    private static final LookupElementBuilder EXTERNALTIMEBATCH;
    private static final LookupElementBuilder TIME;
    private static final LookupElementBuilder FREQUENT;
    private static final LookupElementBuilder LOSSYFREQUENT;
    private static final LookupElementBuilder TIMEBATCH;
    private static final LookupElementBuilder CRON;
    private static final LookupElementBuilder TIMELENGTH;
    private static final LookupElementBuilder EXTERNALTIME;

    //Window Processor Code Snippets with window keyword
    private static final LookupElementBuilder LENGTH_WITH_WINDOW;
    private static final LookupElementBuilder LENGTHBATCH_WITH_WINDOW;
    private static final LookupElementBuilder SORT_WITH_WINDOW;
    private static final LookupElementBuilder EXTERNALTIMEBATCH_WITH_WINDOW;
    private static final LookupElementBuilder TIME_WITH_WINDOW;
    private static final LookupElementBuilder FREQUENT_WITH_WINDOW;
    private static final LookupElementBuilder LOSSYFREQUENT_WITH_WINDOW;
    private static final LookupElementBuilder TIMEBATCH_WITH_WINDOW;
    private static final LookupElementBuilder CRON_WITH_WINDOW;
    private static final LookupElementBuilder TIMELENGTH_WITH_WINDOW;
    private static final LookupElementBuilder EXTERNALTIME_WITH_WINDOW;

    //Language type keywords
    private static final LookupElementBuilder JAVASCRIPT;
    private static final LookupElementBuilder SCALA;
    private static final LookupElementBuilder R;

    //Joins
    private static final LookupElementBuilder LEFT_OUTER_JOIN;
    private static final LookupElementBuilder RIGHT_OUTER_JOIN;
    private static final LookupElementBuilder FULL_OUTER_JOIN;
    private static final LookupElementBuilder OUTER_JOIN;
    private static final LookupElementBuilder INNER_JOIN;

    //Expression
    private static final LookupElementBuilder EXPRESSION_WITH_HASH;
    private static final LookupElementBuilder EXPRESSION_WITHOUT_HASH;

    //Siddhi Log
    private static final LookupElementBuilder LOG;

    private static final LookupElementBuilder AS_WITH_ALIAS;
    private static final LookupElementBuilder ON_WITH_EXPRESSION;

    private static final LookupElementBuilder ENTER_YOUR_EXPRESSION;

    private static final LookupElementBuilder WITH_AND_PARENTHESES;

    //Other Keywords
    private static final LookupElementBuilder SET;
    private static final LookupElementBuilder SELECT;
    private static final LookupElementBuilder GROUP;
    private static final LookupElementBuilder ORDER;
    private static final LookupElementBuilder LIMIT;
    private static final LookupElementBuilder ASC;
    private static final LookupElementBuilder DESC;
    private static final LookupElementBuilder BY;
    private static final LookupElementBuilder HAVING;
    private static final LookupElementBuilder INSERT;
    private static final LookupElementBuilder DELETE;
    private static final LookupElementBuilder UPDATE_OR_INSERT_INTO;
    private static final LookupElementBuilder UPDATE;
    private static final LookupElementBuilder RETURN;
    private static final LookupElementBuilder EVENTS;
    private static final LookupElementBuilder INTO;
    private static final LookupElementBuilder OUTPUT;
    private static final LookupElementBuilder SNAPSHOT;
    private static final LookupElementBuilder FOR;
    private static final LookupElementBuilder RAW;
    private static final LookupElementBuilder OF;
    private static final LookupElementBuilder AS;
    private static final LookupElementBuilder ON;
    private static final LookupElementBuilder WITHIN;
    private static final LookupElementBuilder WITH;
    private static final LookupElementBuilder BEGIN;
    private static final LookupElementBuilder END;
    private static final LookupElementBuilder NULL;
    private static final LookupElementBuilder ALL;
    private static final LookupElementBuilder LAST;
    private static final LookupElementBuilder FIRST;
    private static final LookupElementBuilder JOIN;
    private static final LookupElementBuilder INNER;
    private static final LookupElementBuilder OUTER;
    private static final LookupElementBuilder RIGHT;
    private static final LookupElementBuilder LEFT;
    private static final LookupElementBuilder FULL;
    private static final LookupElementBuilder UNIDIRECTIONAL;
    private static final LookupElementBuilder FALSE;
    private static final LookupElementBuilder TRUE;
    private static final LookupElementBuilder AGGREGATION;
    private static final LookupElementBuilder AGGREGATE;
    private static final LookupElementBuilder PER;


    static {
        DEFINE = createKeywordLookupElement("define");
        PARTITION = createKeywordLookupElement("partition");
        FROM = createKeywordLookupElement("from");
        AT_SYMBOL = createKeywordLookupElement("@");
        HASH_SYMBOL = createKeywordLookupElement("#");
        COMMA_SYMBOL = createKeywordLookupElement(",");
        SEMI_COLON_SYMBOL = createKeywordLookupElement(";");
        QUERY_SNIIP = createDefineSnippetTypeLookupElement("from stream_name\n" +
                "select attribute1 , attribute2\n" +
                "insert into output_stream").withPresentableText("query");
        QUERY_PATTERN_SNIP = createDefineSnippetTypeLookupElement("from every stream_reference=stream_name" +
                "[filter_condition] -> \n" +
                "    every stream_reference2=stream_name2[filter_condition2]\n" +
                "    within  time_gap\n" +
                "select stream_reference.attribute1, stream_reference.attribute1\n" +
                "insert into output_stream").withPresentableText("query-Pattern");
        QUERY_JOIN_SNIIP = createDefineSnippetTypeLookupElement("from stream_name[filter_condition]#window" +
                ".window_name" +
                "(args) as reference\n" +
                "    join stream_name[filter_condition]#window.window_name(args) as reference\n" +
                "    on join_condition\n" +
                "    within  time_gap\n" +
                "select attribute1, attribute2\n" +
                "insert into output_stream").withPresentableText("query-Join");
        QUERY_WINDOW_FILTER_SNIIP = createDefineSnippetTypeLookupElement("from stream_name[filter_condition]#window" +
                ".namespace:window_name(args)\n" +
                "select attribute1 , attribute2\n" +
                "insert into output_stream").withPresentableText("query-windowFilter");
        QUERY_WINDOW_SNIIP = createDefineSnippetTypeLookupElement("from stream_name#window.namespace:window_name" +
                "(args)" +
                "\n" +
                "select attribute1, attribute2\n" +
                "insert into output_stream").withPresentableText("query-Window");
        QUERY_FILTER_SNIIP = createDefineSnippetTypeLookupElement("from stream_name[filter_condition]\n" +
                "select attribute1, attribute2\n" +
                "insert into output_stream").withPresentableText("query-Filter");
        ANNOTATION_SINK = createDefineSnippetTypeLookupElement("@sink(type='sink_type', " +
                "static_option_key='static_option_value', dynamic_option_key='{{dynamic_option_value}}',\n" +
                "    @map(type='map_type', static_option_key='static_option_value', " +
                "dynamic_option_key='{{dynamic_option_value}}',\n" +
                "        @payload( 'payload_mapping')\n" +
                "    )\n" +
                ")\n" +
                "define stream stream_name (attribute1 Type1, attributeN TypeN);").withPresentableText
                ("annotation-Sink");
        ANNOTATION_SOURCE = createDefineSnippetTypeLookupElement("@source(type='source_type', " +
                "static_option_key='static_option_value', dynamic_option_key='{{dynamic_option_value}}',\n" +
                "    @map(type='map_type', static_option_key='static_option_value', " +
                "dynamic_option_key='{{dynamic_option_value}}',\n" +
                "        @attributes( 'attribute_mapping_1', 'attribute_mapping_N')\n" +
                "    )\n" +
                ")\n" +
                "define stream stream_name (attribute1 Type1, attributeN TypeN);")
                .withPresentableText("annotation-Source");
        PARTITION_SNIIP = createDefineSnippetTypeLookupElement("partition with (attribute_name of stream_name, " +
                "attribute2_name of stream2_name)\n" +
                "begin\n" +
                "    queries\n" +
                "end;").withPresentableText("partition");
        ANNOTATION_CONFIG_SNIIP = createDefineSnippetTypeLookupElement("@config(async = 'true')")
                .withPresentableText("annotation-Config");
        DEFINITION_STREAM = createDefineSnippetTypeLookupElement("define stream stream_name (attr1 Type1, attN " +
                "TypeN);").withPresentableText("define-Stream");
        ANNOTATION_EXPORTSTREAM = createDefineSnippetTypeLookupElement("@Export(\"Stream_ID\")")
                .withPresentableText("annotation-ExportStream");
        ANNOTATION_IMPORTSTREAM = createDefineSnippetTypeLookupElement("@Import(\"Stream_ID\")")
                .withPresentableText("annotaion-ImportStream");
        ANNOTATION_APPTRACE = createDefineSnippetTypeLookupElement("@App:Trace(\"App_Trace\")")
                .withPresentableText("annotation-AppTrace");
        ANNOTATION_APPSTATS = createDefineSnippetTypeLookupElement("@App:Statistics(\"App_Statistics\")")
                .withPresentableText("annotation-AppStatistics");
        ANNOTATION_APPDESC = createDefineSnippetTypeLookupElement("@App:Description(\"App_Description\")")
                .withPresentableText("annotation-AppDesc");
        ANNOTATION_APPNAME = createDefineSnippetTypeLookupElement("@App:name(\"App_Name\")")
                .withPresentableText("annotation-AppName");
        ANNOTATION_PRIMARYKEY = createDefineSnippetTypeLookupElement("@PrimaryKey('attribute_name')")
                .withPresentableText("annotation-PrimaryKey");
        ANNOTATION_INDEX = createDefineSnippetTypeLookupElement("@Index('attribute_name')").withPresentableText
                ("annotation-Index");
        DEFINE_FUNCTION = createDefineSnippetTypeLookupElement("define function function_name[lang_name] return " +
                "return_type { \n" +
                "    function_body \n" +
                "};").withPresentableText("define-Function");
        DEFINE_TRIGGER = createDefineSnippetTypeLookupElement("define trigger trigger_name at time;")
                .withPresentableText("define-Trigger");
        DEFINE_WINDOW = createDefineSnippetTypeLookupElement("define window window_name (attr1 Type1, attN TypeN) " +
                "window_type output event_type events;").withPresentableText("define-Window");
        DEFINE_TABLE = createDefineSnippetTypeLookupElement("define table table_name (attr1 Type1, attN TypeN);")
                .withPresentableText("define-Table");
        DEFINE_AGGREGATION = createDefineSnippetTypeLookupElement("define aggregation aggregation_name " +
                "\nfrom input_stream" +
                "\nselect attribute_name, aggregate_function() (attribute_name) as attribute_name, ..." +
                "\n\tgroup by attribute_name" +
                "\n\taggregate by timestamp_attribute every time_periods;")
                .withPresentableText("define-Aggregation");
        ANNOTATION_INFO = createDefineSnippetTypeLookupElement("@info(name = \"Stream_ID\")")
                .withPresentableText("annotation-Info");

        ANNOTATION_SINK2 = createDefineSnippetTypeLookupElement("sink(type='sink_type', option_key='option_value', " +
                "...)");
        ANNOTATION_SOURCE2 = createDefineSnippetTypeLookupElement("source(type='source_type', " +
                "option_key='option_value', ...) ");
        ANNOTATION_CONFIG_SNIIP2 = createDefineSnippetTypeLookupElement("config(async = 'true')");
        ANNOTATION_EXPORTSTREAM2 = createDefineSnippetTypeLookupElement("Export(\"Stream_ID\")");
        ANNOTATION_IMPORTSTREAM2 = createDefineSnippetTypeLookupElement("Import(\"Stream_ID\")");
        ANNOTATION_APPTRACE2 = createDefineSnippetTypeLookupElement("App:Trace(\"App_Trace\")");
        ANNOTATION_APPSTATS2 = createDefineSnippetTypeLookupElement("App:Statistics(\"App_Statistics\")");
        ANNOTATION_APPDESC2 = createDefineSnippetTypeLookupElement("App:Description(\"App_Description\")");
        ANNOTATION_APPNAME2 = createDefineSnippetTypeLookupElement("App:name(\"App_Name\")");
        ANNOTATION_PRIMARYKEY2 = createDefineSnippetTypeLookupElement("PrimaryKey('attribute_name')");
        ANNOTATION_INDEX2 = createDefineSnippetTypeLookupElement("Index('attribute_name')");
        ANNOTATION_INFO2 = createDefineSnippetTypeLookupElement("info(name = \"Stream_ID\")");
        ANNOTATION_MAP = createDefineSnippetTypeLookupElement("map(type='map_type', option_key='option_value', ...)");
        ANNOTATION_ATTRIBUTES = createDefineSnippetTypeLookupElement("attributes('attribute_mapping_a', " +
                "'attribute_mapping_b') ");
        ANNOTATION_PAYLOARD = createDefineSnippetTypeLookupElement("payload(type='payload_string')");

        STREAM = createKeywordLookupElement("stream");
        TABLE = createKeywordLookupElement("table");
        TRIGGER = createKeywordLookupElement("trigger");
        FUNCTION = createKeywordLookupElement("function");
        WINDOW = createKeywordLookupElement("window");

        OR = createKeywordLookupElement("or");
        AND = createKeywordLookupElement("and");
        NOT = createKeywordLookupElement("not");
        IN = createKeywordLookupElement("in");
        IS = createKeywordLookupElement("is");

        ALL_EVENTS = createKeywordLookupElement("all events");
        EXPIRED_EVENTS = createKeywordLookupElement("expired events");
        CURRENT_EVENTS = createKeywordLookupElement("current events");

        YEARS = createKeywordLookupElement("years");
        MONTHS = createKeywordLookupElement("months");
        WEEKS = createKeywordLookupElement("weeks");
        DAYS = createKeywordLookupElement("days");
        HOURS = createKeywordLookupElement("hours");
        MINUTES = createKeywordLookupElement("minutes");
        SECONDS = createKeywordLookupElement("seconds");
        MILLISECONDS = createKeywordLookupElement("milliseconds");

        AT = createKeywordLookupElement("at");
        EVERY = createKeywordLookupElement("every");
        RETURN = createKeywordLookupElement("return");

        JAVASCRIPT = createKeywordWithoutEndWhitespaceLookupElement("JavaScript");
        SCALA = createKeywordWithoutEndWhitespaceLookupElement("Scala");
        R = createKeywordWithoutEndWhitespaceLookupElement("R");

        SELECT = createKeywordLookupElement("select");
        GROUP = createKeywordLookupElement("group");
        ORDER = createKeywordLookupElement("order");
        ASC = createKeywordLookupElement("asc");
        DESC = createKeywordLookupElement("desc");
        LIMIT = createKeywordLookupElement("limit");
        BY = createKeywordLookupElement("by");
        HAVING = createKeywordLookupElement("having");
        INSERT = createKeywordLookupElement("insert");
        DELETE = createKeywordLookupElement("delete");
        UPDATE_OR_INSERT_INTO = createKeywordLookupElement("update or insert into");
        UPDATE = createKeywordLookupElement("update");

        SET = createKeywordLookupElement("set");
        EVENTS = createKeywordLookupElement("events");
        INTO = createKeywordLookupElement("into");
        OUTPUT = createKeywordLookupElement("output");
        SNAPSHOT = createKeywordLookupElement("snapshot");
        FOR = createKeywordLookupElement("for");
        RAW = createKeywordLookupElement("raw");
        OF = createKeywordLookupElement("of");
        AS = createKeywordLookupElement("as");
        ON = createKeywordLookupElement("on");
        WITHIN = createKeywordLookupElement("within");
        WITH = createKeywordLookupElement("with");
        BEGIN = createKeywordLookupElement("begin");
        END = createKeywordLookupElement("end");
        NULL = createKeywordLookupElement("null");
        IS_NULL = createKeywordLookupElement("is null");

        LEFT_OUTER_JOIN = createKeywordLookupElement("left outer join");
        RIGHT_OUTER_JOIN = createKeywordLookupElement("right outer join");
        FULL_OUTER_JOIN = createKeywordLookupElement("full outer join");
        OUTER_JOIN = createKeywordLookupElement("outer join");
        INNER_JOIN = createKeywordLookupElement("inner join");

        EXPRESSION_WITH_HASH = createLookupElementWithCustomTypeText("#[\"enter your expression here\"]",
                "expression").withPresentableText("#expression");
        EXPRESSION_WITHOUT_HASH = createLookupElementWithCustomTypeText("[\"enter your expression here\"]",
                "expression").withPresentableText("expression");
        LOG = createLookupElementWithCustomTypeText("#log(priority, log.message, is.event.logged)",
                "stream processor").withPresentableText("log()");

        AS_WITH_ALIAS = createKeywordLookupElement("as enter_alias_here").withPresentableText("as");
        ON_WITH_EXPRESSION = createKeywordLookupElement("on enter_your_expression").withPresentableText("on");

        ENTER_YOUR_EXPRESSION = createKeywordLookupElement("enter_your_expression");

        WITH_AND_PARENTHESES = createKeywordLookupElement("with ( )");

        ALL = createKeywordLookupElement("all");
        LAST = createKeywordLookupElement("last");
        FIRST = createKeywordLookupElement("first");
        JOIN = createKeywordLookupElement("join");
        INNER = createKeywordLookupElement("inner");
        OUTER = createKeywordLookupElement("outer");
        RIGHT = createKeywordLookupElement("right");
        LEFT = createKeywordLookupElement("left");
        FULL = createKeywordLookupElement("full");
        UNIDIRECTIONAL = createKeywordLookupElement("unidirectional");

        FALSE = createKeywordLookupElement("false");
        TRUE = createKeywordLookupElement("true");

        AGGREGATION = createKeywordLookupElement("aggregation");
        AGGREGATE = createKeywordLookupElement("aggregate");
        PER = createKeywordLookupElement("per");


        STRING = createDataTypeLookupElement("string");
        INT = createDataTypeLookupElement("int");
        LONG = createDataTypeLookupElement("long");
        FLOAT = createDataTypeLookupElement("float");
        DOUBLE = createDataTypeLookupElement("double");
        BOOL = createDataTypeLookupElement("bool");
        OBJECT = createDataTypeLookupElement("object");

        //window types snippets without window keyword
        LENGTH = createWindowProcessorTypeLookupElement("length(window.length)")
                .withPresentableText("length");
        LENGTHBATCH = createWindowProcessorTypeLookupElement("lengthBatch(window.length)")
                .withPresentableText("lengthBatch");
        SORT = createWindowProcessorTypeLookupElement("sort(window.length, attribute, order)")
                .withPresentableText("sort");
        EXTERNALTIMEBATCH = createWindowProcessorTypeLookupElement("externalTimeBatch(timestamp, window.time, start" +
                ".time, timeout)").withPresentableText("externalTimeBatch");
        TIME = createWindowProcessorTypeLookupElement("time(window.time)").withPresentableText("time");
        FREQUENT = createWindowProcessorTypeLookupElement("frequent(event.count, attribute)")
                .withPresentableText("frequent");
        LOSSYFREQUENT = createWindowProcessorTypeLookupElement("lossyFrequent(support.threshold, error.bound, " +
                "attribute)").withPresentableText("lossyFrequent");
        TIMEBATCH = createWindowProcessorTypeLookupElement("timeBatch(window.time, start.time)")
                .withPresentableText("timeBatch");
        CRON = createWindowProcessorTypeLookupElement("cron(cron.expression)").withPresentableText("cron");
        TIMELENGTH = createWindowProcessorTypeLookupElement("timeLength(window.time, window.length)")
                .withPresentableText("timeLength");
        EXTERNALTIME = createWindowProcessorTypeLookupElement("externalTime(window.time)")
                .withPresentableText("externalTime");

        //window types snippets with window keyword
        LENGTH_WITH_WINDOW = createWindowProcessorTypeLookupElement("#window.length(window.length)")
                .withPresentableText("length window");
        LENGTHBATCH_WITH_WINDOW = createWindowProcessorTypeLookupElement("#window.lengthBatch(window.length)")
                .withPresentableText("lengthBatch window");
        SORT_WITH_WINDOW = createWindowProcessorTypeLookupElement("#window.sort(window.length, attribute, order)")
                .withPresentableText("sort window");
        EXTERNALTIMEBATCH_WITH_WINDOW = createWindowProcessorTypeLookupElement("#window.externalTimeBatch(timestamp," +
                " window.time, start.time, timeout)").withPresentableText("externalTimeBatch window");
        TIME_WITH_WINDOW = createWindowProcessorTypeLookupElement("#window.time(window.time)")
                .withPresentableText("time window");
        FREQUENT_WITH_WINDOW = createWindowProcessorTypeLookupElement("#window.frequent(event.count, attribute)")
                .withPresentableText("frequent window");
        LOSSYFREQUENT_WITH_WINDOW = createWindowProcessorTypeLookupElement("#window.lossyFrequent(support.threshold," +
                " error.bound,attribute)").withPresentableText("lossyFrequent window");
        TIMEBATCH_WITH_WINDOW = createWindowProcessorTypeLookupElement("#window.timeBatch(window.time, start.time)")
                .withPresentableText("timeBatch window");
        CRON_WITH_WINDOW = createWindowProcessorTypeLookupElement("#window.cron(cron.expression)")
                .withPresentableText("cron window");
        TIMELENGTH_WITH_WINDOW = createWindowProcessorTypeLookupElement("#window.timeLength(window.time, window" +
                        ".length)").withPresentableText("timeLength window");
        EXTERNALTIME_WITH_WINDOW = createWindowProcessorTypeLookupElement("#window.externalTime(window.time)")
                .withPresentableText("externalTime window");
    }

    private SiddhiCompletionUtils() {

    }

    /**
     * Creates a lookup element.
     *
     * @param name          name of the lookup
     * @param insertHandler insert handler of the lookup
     * @return {@link LookupElementBuilder} which will be used to create the lookup element.
     */
    @NotNull
    private static LookupElementBuilder createLookupElement(@NotNull String name,
                                                            @Nullable InsertHandler<LookupElement> insertHandler) {
        return LookupElementBuilder.create(name).withBoldness(true).withInsertHandler(insertHandler);
    }

    /**
     * Creates a keyword lookup element.
     *
     * @param name name of the lookup
     * @return {@link LookupElementBuilder} which will be used to create the lookup element.
     */
    @NotNull
    private static LookupElementBuilder createKeywordLookupElement(@NotNull String name) {
        return createLookupElement(name, createTemplateBasedInsertHandler("siddhi_lang_" + name));
    }

    /**
     * Creates a keyword lookup element and doesn't add a space after the word suggested.
     *
     * @param name name of the lookup
     * @return {@link LookupElementBuilder} which will be used to create the lookup element.
     */
    @NotNull
    private static LookupElementBuilder createKeywordWithoutEndWhitespaceLookupElement(@NotNull String name) {
        return createLookupElement(name, null);
    }

    @NotNull
    private static InsertHandler<LookupElement> createTemplateBasedInsertHandler(@NotNull String templateId) {
        return (context, item) -> {
            Template template = TemplateSettings.getInstance().getTemplateById(templateId);
            Editor editor = context.getEditor();
            if (template != null) {
                editor.getDocument().deleteString(context.getStartOffset(), context.getTailOffset());
                TemplateManager.getInstance(context.getProject()).startTemplate(editor, template);
            } else {
                int currentOffset = editor.getCaretModel().getOffset();
                CharSequence documentText = editor.getDocument().getImmutableCharSequence();
                if (documentText.length() <= currentOffset || documentText.charAt(currentOffset) != ' ') {
                    EditorModificationUtil.insertStringAtCaret(editor, " ");
                } else {
                    EditorModificationUtil.moveCaretRelatively(editor, 1);
                }
            }
        };
    }

    /**
     * Creates a <b>Type</b> lookup element.
     *
     * @param name of the lookup
     * @return {@link LookupElementBuilder} which will be used to create the lookup element.
     */
    @NotNull
    private static LookupElementBuilder createDataTypeLookupElement(@NotNull String name) {
        return createLookupElement(name, null).withTypeText("Data Type");
    }

    /**
     * Creates a <b>Type</b> lookup element.
     *
     * @param name of the lookup
     * @return {@link LookupElementBuilder} which will be used to create the lookup element.
     */
    @NotNull
    private static LookupElementBuilder createWindowProcessorTypeLookupElement(@NotNull String name) {
        return createLookupElement(name, null).withTypeText("Window Processor");
    }

    /**
     * Creates a <b>Type</b> lookup element.
     *
     * @param name of the lookup
     * @return {@link LookupElementBuilder} which will be used to create the lookup element.
     */
    @NotNull
    private static LookupElementBuilder createDefineSnippetTypeLookupElement(@NotNull String name) {
        return createLookupElement(name, null).withTypeText("Snippet");
    }

    /**
     * Creates a <b>Type</b> lookup element with a user given Type Text.
     *
     * @param name of the lookup
     * @param text string which needed to be shown as type text
     * @return {@link LookupElementBuilder} which will be used to create the lookup element.
     */
    @NotNull
    private static LookupElementBuilder createLookupElementWithCustomTypeText(@NotNull String name,
                                                                              @NotNull String text) {
        return createLookupElement(name, null).withTypeText(text);
    }


    /**
     * Adds value types as lookups.
     *
     * @param resultSet result list which is used to add lookups
     */
    public static void addValueTypesAsLookups(@NotNull CompletionResultSet resultSet) {
        resultSet.addElement(PrioritizedLookupElement.withPriority(LONG, VALUE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(INT, VALUE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(FLOAT, VALUE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(STRING, VALUE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(BOOL, VALUE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(DOUBLE, VALUE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(OBJECT, VALUE_TYPES_PRIORITY));
    }

    /**
     * Adds Initial declaration types as lookups.
     *
     * @param resultSet result list which is used to add lookups
     */
    public static void addInitialTypesAsLookups(@NotNull CompletionResultSet resultSet) {
        resultSet.addElement(PrioritizedLookupElement.withPriority(DEFINE, VALUE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(PARTITION, VALUE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(FROM, VALUE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(AT_SYMBOL, VALUE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(QUERY_SNIIP, VALUE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(QUERY_PATTERN_SNIP, VALUE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(QUERY_JOIN_SNIIP, VALUE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(QUERY_WINDOW_FILTER_SNIIP, VALUE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(QUERY_FILTER_SNIIP, VALUE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(QUERY_WINDOW_SNIIP, VALUE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(ANNOTATION_SINK, VALUE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(ANNOTATION_SOURCE, VALUE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(PARTITION_SNIIP, VALUE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(ANNOTATION_CONFIG_SNIIP, VALUE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(DEFINITION_STREAM, VALUE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(ANNOTATION_EXPORTSTREAM, VALUE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(ANNOTATION_IMPORTSTREAM, VALUE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(ANNOTATION_APPTRACE, VALUE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(ANNOTATION_APPSTATS, VALUE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(ANNOTATION_APPDESC, VALUE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(ANNOTATION_APPNAME, VALUE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(ANNOTATION_PRIMARYKEY, VALUE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(ANNOTATION_INDEX, VALUE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(DEFINE_FUNCTION, VALUE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(DEFINE_TRIGGER, VALUE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(DEFINE_WINDOW, VALUE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(DEFINE_TABLE, VALUE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(DEFINE_AGGREGATION, VALUE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(ANNOTATION_INFO, VALUE_TYPES_PRIORITY));
    }

    /**
     * Adds query sniipet as a lookup.
     *
     * @param resultSet result list which is used to add lookups
     */
    public static void addQuerySnippetAsLookup(@NotNull CompletionResultSet resultSet) {
        resultSet.addElement(PrioritizedLookupElement.withPriority(QUERY_SNIIP, VALUE_TYPES_PRIORITY));
    }

    /**
     * Adds suggestions after @ symbol as lookups.
     *
     * @param resultSet result list which is used to add lookups
     */
    public static void addAfterATSymbolLookups(@NotNull CompletionResultSet resultSet) {
        resultSet.addElement(PrioritizedLookupElement.withPriority(ANNOTATION_SINK2, VALUE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(ANNOTATION_SOURCE2, VALUE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(ANNOTATION_CONFIG_SNIIP2, VALUE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(ANNOTATION_EXPORTSTREAM2, VALUE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(ANNOTATION_IMPORTSTREAM2, VALUE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(ANNOTATION_APPTRACE2, VALUE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(ANNOTATION_APPSTATS2, VALUE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(ANNOTATION_APPDESC2, VALUE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(ANNOTATION_APPNAME2, VALUE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(ANNOTATION_PRIMARYKEY2, VALUE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(ANNOTATION_INDEX2, VALUE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(ANNOTATION_INFO2, VALUE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(ANNOTATION_MAP, VALUE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(ANNOTATION_ATTRIBUTES, VALUE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(ANNOTATION_PAYLOARD, VALUE_TYPES_PRIORITY));
    }

    /**
     * Adds suggestions in beginning of a query_output node as lookups.
     *
     * @param resultSet result list which is used to add lookups
     */
    public static void addBeginingOfQueryOutputKeywords(@NotNull CompletionResultSet resultSet) {
        addKeywordAsLookup(resultSet, INSERT);
        addKeywordAsLookup(resultSet, DELETE);
        addKeywordAsLookup(resultSet, UPDATE_OR_INSERT_INTO);
        addKeywordAsLookup(resultSet, UPDATE);
    }

    /**
     * Adds suggestions after a query_input node as lookups.
     *
     * @param resultSet result list which is used to add lookups
     */
    public static void addSuggestionsAfterQueryInput(@NotNull CompletionResultSet resultSet) {
        addKeywordAsLookup(resultSet, SELECT);
        addKeywordAsLookup(resultSet, OUTPUT);
        addKeywordAsLookup(resultSet, INSERT);
        addKeywordAsLookup(resultSet, DELETE);
        addKeywordAsLookup(resultSet, UPDATE_OR_INSERT_INTO);
        addKeywordAsLookup(resultSet, UPDATE);
    }

    /**
     * Adds value types as lookups.
     *
     * @param resultSet result list which is used to add lookups
     */
    public static void addDefineTypesAsLookups(@NotNull CompletionResultSet resultSet) {
        resultSet.addElement(PrioritizedLookupElement.withPriority(STREAM, VALUE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(TABLE, VALUE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(TRIGGER, VALUE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(FUNCTION, VALUE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(WINDOW, VALUE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(AGGREGATION, VALUE_TYPES_PRIORITY));
    }

    /**
     * Adds a keyword as a lookup.
     *
     * @param resultSet     result list which is used to add lookups
     * @param lookupElement lookup element which needs to be added to the result list
     */
    private static void addKeywordAsLookup(@NotNull CompletionResultSet resultSet,
                                           @NotNull LookupElement lookupElement) {
        resultSet.addElement(PrioritizedLookupElement.withPriority(lookupElement, KEYWORDS_PRIORITY));
    }

    private static LookupElement createKeywordAsLookup(@NotNull LookupElement lookupElement) {
        return PrioritizedLookupElement.withPriority(lookupElement, KEYWORDS_PRIORITY);
    }

    public static void addAtKeyword(@NotNull CompletionResultSet resultSet) {
        addKeywordAsLookup(resultSet, AT);
    }

    public static void addEveryKeyword(@NotNull CompletionResultSet resultSet) {
        addKeywordAsLookup(resultSet, EVERY);
    }

    public static void addFromKeyword(@NotNull CompletionResultSet resultSet) {
        addKeywordAsLookup(resultSet, FROM);
    }

    public static void addSelectKeyword(@NotNull CompletionResultSet resultSet) {
        addKeywordAsLookup(resultSet, SELECT);
    }

    public static void addByKeyword(@NotNull CompletionResultSet resultSet) {
        addKeywordAsLookup(resultSet, BY);
    }

    public static void addHavingKeyword(@NotNull CompletionResultSet resultSet) {
        addKeywordAsLookup(resultSet, HAVING);
    }

    public static void addGroupKeyword(@NotNull CompletionResultSet resultSet) {
        addKeywordAsLookup(resultSet, GROUP);
    }

    public static void addAggregateKeyword(@NotNull CompletionResultSet resultSet) {
        addKeywordAsLookup(resultSet, AGGREGATE);
    }

    public static void addAggregationTimeTypesKeyword(@NotNull CompletionResultSet resultSet) {
        addKeywordAsLookup(resultSet, SECONDS);
        addKeywordAsLookup(resultSet, MINUTES);
        addKeywordAsLookup(resultSet, HOURS);
        addKeywordAsLookup(resultSet, DAYS);
        addKeywordAsLookup(resultSet, WEEKS);
        addKeywordAsLookup(resultSet, MONTHS);
        addKeywordAsLookup(resultSet, YEARS);
    }

    public static void addOrderKeyword(@NotNull CompletionResultSet resultSet) {
        addKeywordAsLookup(resultSet, ORDER);
    }

    public static void addLimitKeyword(@NotNull CompletionResultSet resultSet) {
        addKeywordAsLookup(resultSet, LIMIT);
    }

    public static void addOrderRelatedKeywords(@NotNull CompletionResultSet resultSet) {
        addKeywordAsLookup(resultSet, ASC);
        addKeywordAsLookup(resultSet, DESC);
    }

    public static void addOutputKeyword(@NotNull CompletionResultSet resultSet) {
        addKeywordAsLookup(resultSet, OUTPUT);
    }

    public static void addSnapshotKeyword(@NotNull CompletionResultSet resultSet) {
        addKeywordAsLookup(resultSet, SNAPSHOT);
    }

    public static void addOutputRateTypesKeyword(@NotNull CompletionResultSet resultSet) {
        addKeywordAsLookup(resultSet, ALL);
        addKeywordAsLookup(resultSet, LAST);
        addKeywordAsLookup(resultSet, FIRST);
    }

    public static void addEventsKeyword(@NotNull CompletionResultSet resultSet) {
        addKeywordAsLookup(resultSet, EVENTS);
    }

    public static void addReturnKeyword(@NotNull CompletionResultSet resultSet) {
        addKeywordAsLookup(resultSet, RETURN);
    }

    public static void addIntoKeyword(@NotNull CompletionResultSet resultSet) {
        addKeywordAsLookup(resultSet, INTO);
    }

    public static void addWithinKeyword(@NotNull CompletionResultSet resultSet) {
        addKeywordAsLookup(resultSet, WITHIN);
    }

    public static void addForKeyword(@NotNull CompletionResultSet resultSet) {
        addKeywordAsLookup(resultSet, FOR);
    }

    public static void addOnKeyword(@NotNull CompletionResultSet resultSet) {
        addKeywordAsLookup(resultSet, ON);
    }

    public static void addSetKeyword(@NotNull CompletionResultSet resultSet) {
        addKeywordAsLookup(resultSet, SET);
    }

    public static void addOrKeyword(@NotNull CompletionResultSet resultSet) {
        addKeywordAsLookup(resultSet, OR);
    }

    public static void addAsKeyword(@NotNull CompletionResultSet resultSet) {
        addKeywordAsLookup(resultSet, AS);
    }

    public static void addBeginKeyword(@NotNull CompletionResultSet resultSet) {
        addKeywordAsLookup(resultSet, BEGIN);
    }

    public static void addEndKeyword(@NotNull CompletionResultSet resultSet) {
        addKeywordAsLookup(resultSet, END);
    }

    public static void addOfKeyword(@NotNull CompletionResultSet resultSet) {
        addKeywordAsLookup(resultSet, OF);
    }

    public static void addUnidirectionalKeyword(@NotNull CompletionResultSet resultSet) {
        addKeywordAsLookup(resultSet, UNIDIRECTIONAL);
    }

    public static void addAsKeywordWithDummyAlias(@NotNull CompletionResultSet resultSet) {
        addKeywordAsLookup(resultSet, AS_WITH_ALIAS);
    }

    public static void addEnterYourExpressionClause(@NotNull CompletionResultSet resultSet) {
        addKeywordAsLookup(resultSet, ENTER_YOUR_EXPRESSION);
    }

    public static void addComma(@NotNull CompletionResultSet resultSet) {
        addKeywordAsLookup(resultSet, COMMA_SYMBOL);
    }

    public static void addSemicolon(@NotNull CompletionResultSet resultSet) {
        addKeywordAsLookup(resultSet, SEMI_COLON_SYMBOL);
    }

    public static void addPerKeyword(@NotNull CompletionResultSet resultSet) {
        addKeywordAsLookup(resultSet, PER);
    }

    public static void addOutputEventTypeKeywords(@NotNull CompletionResultSet resultSet) {
        addKeywordAsLookup(resultSet, (ALL_EVENTS));
        addKeywordAsLookup(resultSet, (EXPIRED_EVENTS));
        addKeywordAsLookup(resultSet, (CURRENT_EVENTS));
    }

    public static void addLanguageTypesKeywords(@NotNull CompletionResultSet resultSet) {
        addKeywordAsLookup(resultSet, JAVASCRIPT);
        addKeywordAsLookup(resultSet, R);
        addKeywordAsLookup(resultSet, SCALA);
    }

    public static void addFilterSuggestion(@NotNull CompletionResultSet resultSet) {
        addKeywordAsLookup(resultSet, EXPRESSION_WITH_HASH);
        addKeywordAsLookup(resultSet, EXPRESSION_WITHOUT_HASH);
    }

    public static void addStreamFunctions(@NotNull CompletionResultSet resultSet) {
        addKeywordAsLookup(resultSet, LOG);
    }

    public static void addWithKeywordAndParentheses(@NotNull CompletionResultSet resultSet) {
        addKeywordAsLookup(resultSet, WITH_AND_PARENTHESES);
    }

    public static void addWindowTypesWithWindowKeyword(@NotNull CompletionResultSet resultSet) {
        resultSet.addElement(PrioritizedLookupElement.withPriority(LENGTH_WITH_WINDOW, VALUE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(LENGTHBATCH_WITH_WINDOW, VALUE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(SORT_WITH_WINDOW, VALUE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(EXTERNALTIMEBATCH_WITH_WINDOW,
                VALUE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(TIME_WITH_WINDOW, VALUE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(FREQUENT_WITH_WINDOW, VALUE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(LOSSYFREQUENT_WITH_WINDOW, VALUE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(TIMEBATCH_WITH_WINDOW, VALUE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(CRON_WITH_WINDOW, VALUE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(TIMELENGTH_WITH_WINDOW, VALUE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(EXTERNALTIME_WITH_WINDOW, VALUE_TYPES_PRIORITY));
    }

    public static void addSuggestionsRelatedToJoins(@NotNull CompletionResultSet resultSet) {
        addKeywordAsLookup(resultSet, LEFT_OUTER_JOIN);
        addKeywordAsLookup(resultSet, RIGHT_OUTER_JOIN);
        addKeywordAsLookup(resultSet, FULL_OUTER_JOIN);
        addKeywordAsLookup(resultSet, OUTER_JOIN);
        addKeywordAsLookup(resultSet, INNER_JOIN);
        addKeywordAsLookup(resultSet, JOIN);
    }

    public static void onWithExpressionKeyword(@NotNull CompletionResultSet resultSet) {
        addKeywordAsLookup(resultSet, ON_WITH_EXPRESSION);
    }

    /**
     * Adds window types as lookups.
     *
     * @param resultSet result list which is used to add lookups
     */
    public static void addWindowProcessorTypesAsLookups(@NotNull CompletionResultSet resultSet) {
        resultSet.addElement(PrioritizedLookupElement.withPriority(LENGTH, VALUE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(LENGTHBATCH, VALUE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(SORT, VALUE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(EXTERNALTIMEBATCH, VALUE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(TIME, VALUE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(FREQUENT, VALUE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(LOSSYFREQUENT, VALUE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(TIMEBATCH, VALUE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(CRON, VALUE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(TIMELENGTH, VALUE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(EXTERNALTIME, VALUE_TYPES_PRIORITY));
    }

    public static void addValueKeywords(@NotNull CompletionResultSet resultSet) {
        addKeywordAsLookup(resultSet, TRUE);
        addKeywordAsLookup(resultSet, FALSE);
        addKeywordAsLookup(resultSet, NULL);
    }

    @NotNull
    public static List<LookupElement> createValueKeywords() {
        List<LookupElement> lookupElements = new LinkedList<>();
        lookupElements.add(createKeywordAsLookup(TRUE));
        lookupElements.add(createKeywordAsLookup(FALSE));
        lookupElements.add(createKeywordAsLookup(NULL));
        return lookupElements;
    }

    @NotNull
    private static LookupElement createSourceLookupElement(@NotNull PsiElement element) {
        String definitionType = "Source";
        if (PsiTreeUtil.getParentOfType(element, StreamDefinitionNode.class) != null) {
            definitionType = "Stream";
        }
        if (PsiTreeUtil.getParentOfType(element, WindowDefinitionNode.class) != null) {
            definitionType = "Event Window";
        }
        if (PsiTreeUtil.getParentOfType(element, TableDefinitionNode.class) != null) {
            definitionType = "Event Table";
        }
        LookupElementBuilder builder = LookupElementBuilder.create(element.getText())
                .withTypeText(definitionType).withIcon(SiddhiIcons.METHOD);
        return PrioritizedLookupElement.withPriority(builder, VARIABLE_PRIORITY);

    }

    @NotNull
    public static List<LookupElement> createSourceLookupElements(@NotNull Object[] streamIdNodes) {
        List<LookupElement> lookupElements = new LinkedList<>();
        for (Object streamIdNode : streamIdNodes) {
            PsiElement psiElement = (PsiElement) streamIdNode;
            LookupElement lookupElement = SiddhiCompletionUtils.createSourceLookupElement(psiElement);
            lookupElements.add(lookupElement);
        }
        return lookupElements;
    }

    @NotNull
    private static LookupElement createEventTableLookupElement(@NotNull PsiElement element) {
        LookupElementBuilder builder = LookupElementBuilder.create(element.getText())
                .withTypeText("Event Table").withIcon(SiddhiIcons.METHOD);
        return PrioritizedLookupElement.withPriority(builder, VARIABLE_PRIORITY);
    }

    @NotNull
    public static List<LookupElement> createEventTableLookupElements(@NotNull Object[] streamIdNodes) {
        List<LookupElement> lookupElements = new LinkedList<>();
        for (Object streamIdNode : streamIdNodes) {
            PsiElement psiElement = (PsiElement) streamIdNode;
            LookupElement lookupElement = SiddhiCompletionUtils.createEventTableLookupElement(psiElement);
            lookupElements.add(lookupElement);
        }
        return lookupElements;
    }

    @NotNull
    private static LookupElement createAttributeNameLookupElement(@NotNull PsiElement element) {
        LookupElementBuilder builder = LookupElementBuilder.create(element.getText())
                .withTypeText("Attribute Name").withIcon(SiddhiIcons.PROPERTY);
        return PrioritizedLookupElement.withPriority(builder, VARIABLE_PRIORITY);
    }

    @NotNull
    public static List<LookupElement> createAttributeNameLookupElements(@NotNull Object[] attributeNameNodes) {
        List<LookupElement> lookupElements = new LinkedList<>();
        for (Object attributeNameNode : attributeNameNodes) {
            PsiElement psiElement = (PsiElement) attributeNameNode;
            LookupElement lookupElement = SiddhiCompletionUtils.createAttributeNameLookupElement(psiElement);
            lookupElements.add(lookupElement);
        }
        return lookupElements;
    }
}
