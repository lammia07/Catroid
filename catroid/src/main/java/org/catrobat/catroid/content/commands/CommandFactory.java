/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.content.commands;

import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.ConcurrentFormulaHashMap;
import org.catrobat.catroid.content.bricks.FormulaBrick;
import org.catrobat.catroid.formulaeditor.Formula;

public final class CommandFactory {

	private CommandFactory() {
	}

	public static ChangeFormulaCommand makeChangeFormulaCommand(FormulaBrick formulaBrick, Brick.BrickField brickField,
			Formula newFormula) {
		ConcurrentFormulaHashMap newFormulaMap = new ConcurrentFormulaHashMap();
		newFormulaMap.putIfAbsent(brickField, newFormula);
		return makeChangeFormulaCommand(formulaBrick, newFormulaMap);
	}

	public static ChangeFormulaCommand makeChangeFormulaCommand(FormulaBrick formulaBrick,
			ConcurrentFormulaHashMap newFormulaMap) {
		return new ChangeFormulaCommand(formulaBrick, newFormulaMap);
	}
}
