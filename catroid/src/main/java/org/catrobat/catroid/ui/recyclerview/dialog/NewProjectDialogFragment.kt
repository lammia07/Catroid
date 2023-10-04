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
package org.catrobat.catroid.ui.recyclerview.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RadioGroup
import androidx.fragment.app.DialogFragment
import com.google.android.material.switchmaterial.SwitchMaterial
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.common.DefaultProjectHandler.ProjectCreatorType
import org.catrobat.catroid.common.Nameable
import org.catrobat.catroid.merge.NewProjectNameTextWatcher
import org.catrobat.catroid.ui.ProjectActivity
import org.catrobat.catroid.ui.recyclerview.util.UniqueNameProvider
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment
import org.catrobat.catroid.utils.ToastUtil
import java.io.IOException
import org.koin.android.ext.android.inject

class NewProjectDialogFragment : DialogFragment() {

    private var exampleProject = false
    private var castProject = false
    private var landscape = false
    private val projectManager: ProjectManager by inject()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = View.inflate(activity, R.layout.dialog_new_project, null)
        val radioGroup = view.findViewById<RadioGroup>(R.id.radio_group)
        val exampleProjectSwitch = view.findViewById<SwitchMaterial>(R.id.example_project_switch)
        if (SettingsFragment.isCastSharedPreferenceEnabled(activity)) {
            view.findViewById<View>(R.id.cast_radio_button).visibility = View.VISIBLE
        }
        val uniqueNameProvider: UniqueNameProvider = object : UniqueNameProvider() {
            override fun isUnique(newName: String): Boolean {
                return !ReplaceExistingProjectDialogFragment.projectExistsInDirectory(newName)
            }
        }
        val builder = TextInputDialog.Builder(requireContext())
            .setHint(getString(R.string.project_name_label))
            .setText(
                uniqueNameProvider.getUniqueName(
                    getString(R.string.default_project_name),
                    null
                )
            )
            .setTextWatcher(NewProjectNameTextWatcher<Nameable>())
            .setPositiveButton(
                getString(R.string.ok),
                TextInputDialog.OnClickListener { _: DialogInterface?, textInput: String? ->
                    exampleProject = exampleProjectSwitch.isChecked
                    when (radioGroup.checkedRadioButtonId) {
                        R.id.portrait_radio_button -> landscape = false
                        R.id.landscape_mode_radio_button -> landscape = true
                        R.id.cast_radio_button -> castProject = true
                        else -> throw IllegalStateException("$TAG: No radio button id match, check layout?")
                    }
                    createProject(textInput, landscape, exampleProject, castProject)
                })
        return builder
            .setView(view)
            .setNegativeButton(R.string.cancel, null)
            .create()
    }

    fun createProject(
        projectName: String?, landscape: Boolean, exampleProject: Boolean,
        castProject: Boolean
    ) {
        try {
            if (exampleProject) {
                if (castProject) {
                    projectManager
                        .createNewExampleProject(
                            projectName,
                            ProjectCreatorType.PROJECT_CREATOR_CAST,
                            false
                        )
                } else {
                    projectManager
                        .createNewExampleProject(
                            projectName,
                            ProjectCreatorType.PROJECT_CREATOR_DEFAULT,
                            landscape
                        )
                }
            } else {
                if (castProject) {
                    projectManager
                        .createNewEmptyProject(projectName, false, true)
                } else {
                    projectManager
                        .createNewEmptyProject(projectName, landscape, false)
                }
            }
            requireActivity().startActivity(Intent(activity, ProjectActivity::class.java))
        } catch (e: IOException) {
            ToastUtil.showError(activity, R.string.error_new_project)
        }
    }

    companion object {
        val TAG:String = NewProjectDialogFragment::class.java.simpleName
    }
}