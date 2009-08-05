/*
 * Copyright (C) 2009 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.ase.interpreter;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.ase.Constants;
import com.google.ase.interpreter.bsh.BshInterpreter;
import com.google.ase.interpreter.jruby.JRubyInterpreter;
import com.google.ase.interpreter.lua.LuaInterpreter;
import com.google.ase.interpreter.python.PythonInterpreter;
import com.google.ase.interpreter.sh.ShInterpreter;

/**
 * Manages and provides access to the set of available interpreters.
 *
 * @author Damon Kohler (damonkohler@gmail.com)
 */
// TODO(damonkohler): Should probably use a map instead of a list of
// interpreters. But, then we wouldn't be able to make this a utility class and
// the list of interpreters is very small.
public class InterpreterManager {

  private InterpreterManager() {
    // Utility class.
  }

  private final static List<? extends InterpreterInterface> mSupportedInterpreters =
      Arrays.asList(new LuaInterpreter(), new BshInterpreter(), new PythonInterpreter(),
          new ShInterpreter(), new JRubyInterpreter());

  public static boolean checkInstalled(final String interpreterName) {
    if (interpreterName.equals("sh")) {
      // Shell is installed by the system.
      return true;
    }
    File interpreterDirectory = new File(Constants.INTERPRETER_ROOT + interpreterName);
    File interpreterExtrasDirectory = new File(Constants.INTERPRETER_EXTRAS_ROOT + interpreterName);
    return interpreterDirectory.exists() || interpreterExtrasDirectory.exists();
  }

  /**
   * Returns the list of all known interpreters.
   */
  public static List<? extends InterpreterInterface> getSupportedInterpreters() {
    return mSupportedInterpreters;
  }

  public static List<InterpreterInterface> getInstalledInterpreters() {
    List<InterpreterInterface> interpreters = new ArrayList<InterpreterInterface>();
    for (InterpreterInterface i : mSupportedInterpreters) {
      if (checkInstalled(i.getName())) {
        interpreters.add(i);
      }
    }
    return interpreters;
  }

  public static List<InterpreterInterface> getNotInstalledInterpreters() {
    List<InterpreterInterface> interpreters = new ArrayList<InterpreterInterface>();
    for (InterpreterInterface i : mSupportedInterpreters) {
      if (!checkInstalled(i.getName())) {
        interpreters.add(i);
      }
    }
    return interpreters;
  }

  /**
   * Returns the interpreter matching the provided name or null if no
   * interpreter was found.
   */
  public static InterpreterInterface getInterpreterByName(String interpreterName) {
    for (InterpreterInterface i : mSupportedInterpreters) {
      if (i.getName().equals(interpreterName)) {
        return i;
      }
    }
    return null;
  }

  /**
   * Returns the correct interpreter for the provided script name based on the
   * script's extension or null if no interpreter was found.
   */
  public static InterpreterInterface getInterpreterForScript(String scriptName) {
    int dotIndex = scriptName.lastIndexOf('.');
    if (dotIndex == -1) {
      return null;
    }
    String ext = scriptName.substring(dotIndex);
    for (InterpreterInterface i : mSupportedInterpreters) {
      if (i.getExtension().equals(ext)) {
        return i;
      }
    }
    return null;
  }
}
