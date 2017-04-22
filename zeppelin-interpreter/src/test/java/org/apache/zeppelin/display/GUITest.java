/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.zeppelin.display;

import org.apache.commons.io.IOUtils;
import org.apache.zeppelin.display.ui.CheckBox;
import org.apache.zeppelin.display.ui.OptionInput.ParamOption;
import org.apache.zeppelin.display.ui.Select;
import org.apache.zeppelin.display.ui.TextBox;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GUITest {

  private ParamOption[] options = new ParamOption[]{
      new ParamOption("1", "value_1"),
      new ParamOption("2", "value_2")
  };

  private List<Object> checkedItems;

  @Before
  public void setUp() {
    checkedItems = new ArrayList<>();
    checkedItems.add("1");
  }

  @Test
  public void testGson() {
    GUI gui = new GUI();
    gui.textbox("textbox_1", "default_text_1");
    gui.select("select_1", "1", options);
    List<Object> list = new ArrayList();
    list.add("1");
    gui.checkbox("checkbox_1", list, options);

    String json = gui.toJson();
    System.out.println(json);
    GUI gui2 = GUI.fromJson(json);
    assertEquals(gui2.toJson(), json);
    assertEquals(gui2.forms, gui2.forms);
    assertEquals(gui2.params, gui2.params);
  }

  // Case 1. Old input forms are created in backend, in this case type is always set
  @Test
  public void testOldGson_1() throws IOException {

    GUI gui = new GUI();
    gui.forms.put("textbox_1", new OldInput.OldTextBox("textbox_1", "default_text_1"));
    gui.forms.put("select_1", new OldInput.OldSelect("select_1", "1", options));
    gui.forms.put("checkbox_1",
        new OldInput.OldCheckBox("checkbox_1", checkedItems, options));

    // convert to old json format.
    String json = gui.toJson();

    // convert to new input forms
    GUI gui2 = GUI.fromJson(json);
    assertTrue(3 == gui2.forms.size());
    assertTrue(gui2.forms.get("textbox_1") instanceof TextBox);
    assertEquals("default_text_1", gui2.forms.get("textbox_1").getDefaultValue());
    assertTrue(gui2.forms.get("select_1") instanceof Select);
    assertEquals(options, ((Select) gui2.forms.get("select_1")).getOptions());
    assertTrue(gui2.forms.get("checkbox_1") instanceof CheckBox);
    assertEquals(options, ((CheckBox) gui2.forms.get("checkbox_1")).getOptions());
  }

  // Case 2. Old input forms are created in frontend, in this case type is only set for checkbox
  // Actually this is a bug due to method Input#getInputForm
  @Test
  public void testOldGson_2() throws IOException {

    GUI gui = new GUI();
    gui.forms.put("textbox_1", new OldInput("textbox_1", "default_text_1"));
    gui.forms.put("select_1", new OldInput("select_1", "1", options));
    gui.forms.put("checkbox_1",
        new OldInput.OldCheckBox("checkbox_1", checkedItems, options));

    // convert to old json format.
    String json = gui.toJson();

    // convert to new input forms
    GUI gui2 = GUI.fromJson(json);
    assertTrue(3 == gui2.forms.size());
    assertTrue(gui2.forms.get("textbox_1") instanceof TextBox);
    assertEquals("default_text_1", gui2.forms.get("textbox_1").getDefaultValue());
    assertTrue(gui2.forms.get("select_1") instanceof Select);
    assertEquals(options, ((Select) gui2.forms.get("select_1")).getOptions());
    assertTrue(gui2.forms.get("checkbox_1") instanceof CheckBox);
    assertEquals(options, ((CheckBox) gui2.forms.get("checkbox_1")).getOptions());
  }
}
