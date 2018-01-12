/*
 *  Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.siddhi.plugins.idea.singleeventsimulation.ui;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;
import org.wso2.siddhi.plugins.idea.runconfig.SiddhiRunningState;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;


/**
 * The base class for single event simulation implementations.
 */
public class SingleEventSimulationToolWindowFactory implements ToolWindowFactory {

    private JPanel myToolWindowContent;
    private ToolWindow myToolWindow;
    private JComboBox siddhiAppNameComboBox;
    private JComboBox streamNameComboBox;
    private JComboBox timestampMsOptionalComboBox;
    private JFormattedTextField idINTFormattedTextField;
    private JCheckBox checkBox1;
    private JButton sendButton;

    // Create the tool window content.
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull  ToolWindow toolWindow) {
        myToolWindow = toolWindow;
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(myToolWindowContent, "", false);
        toolWindow.getContentManager().addContent(content);

    }

    public void implementation(){
        ActionManager.getInstance();
        //SiddhiRunningState;
    }
}
