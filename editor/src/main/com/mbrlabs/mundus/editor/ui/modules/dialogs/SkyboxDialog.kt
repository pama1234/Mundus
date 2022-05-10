/*
 * Copyright (c) 2016. See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mbrlabs.mundus.editor.ui.modules.dialogs

import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.kotcrab.vis.ui.widget.VisDialog
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisSelectBox
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisTextButton
import com.kotcrab.vis.ui.widget.VisTextField
import com.mbrlabs.mundus.commons.assets.Asset
import com.mbrlabs.mundus.commons.assets.SkyboxAsset
import com.mbrlabs.mundus.commons.skybox.Skybox
import com.mbrlabs.mundus.editor.Mundus
import com.mbrlabs.mundus.editor.assets.AssetAlreadyExistsException
import com.mbrlabs.mundus.editor.core.project.ProjectManager
import com.mbrlabs.mundus.editor.events.LogEvent
import com.mbrlabs.mundus.editor.events.LogType
import com.mbrlabs.mundus.editor.events.ProjectChangedEvent
import com.mbrlabs.mundus.editor.events.SceneChangedEvent
import com.mbrlabs.mundus.editor.shader.Shaders
import com.mbrlabs.mundus.editor.ui.widgets.ImageChooserField
import com.mbrlabs.mundus.editor.utils.createDefaultSkybox
import com.mbrlabs.mundus.editor.utils.createSkyboxFromAsset

/**
 * @author Marcus Brummer
 * @version 10-01-2016
 */
class SkyboxDialog : BaseDialog("Skybox"), ProjectChangedEvent.ProjectChangedListener,
        SceneChangedEvent.SceneChangedListener {

    private lateinit var root: VisTable
    private lateinit var selectBox: VisSelectBox<SkyboxAsset>

    private val positiveX: ImageChooserField = ImageChooserField(100)
    private var negativeX: ImageChooserField = ImageChooserField(100)
    private var positiveY: ImageChooserField = ImageChooserField(100)
    private var negativeY: ImageChooserField = ImageChooserField(100)
    private var positiveZ: ImageChooserField = ImageChooserField(100)
    private var negativeZ: ImageChooserField = ImageChooserField(100)

    private var createBtn = VisTextButton("Create skybox")
    private var defaultBtn = VisTextButton("Create default skybox")
    private var deletBtn = VisTextButton("Remove Skybox")

    private var skyboxName = VisTextField()

    private val projectManager: ProjectManager = Mundus.inject()

    init {
        Mundus.registerEventListener(this)

        setupUI()
        setupListeners()
    }

    private fun setupUI() {
        positiveX.setButtonText("Right (+X)")
        negativeX.setButtonText("Left (-X)")
        positiveY.setButtonText("Top (+Y)")
        negativeY.setButtonText("Bottom (-Y)")
        positiveZ.setButtonText("Front (+Z)")
        negativeZ.setButtonText("Back (-Z)")

        root = VisTable()
        add(root).left().top()

        root.add(VisLabel("Set Skybox")).left().row()
        root.addSeparator().row()

        // Skybox selector
        val selectorsTable = VisTable(true)
        selectorsTable.add(VisLabel("Set Skybox From Existing:"))
        selectBox = VisSelectBox<SkyboxAsset>()
        selectorsTable.add(selectBox).left()
        root.add(selectorsTable).row()

        // Image pickers
        root.add(VisLabel("Create a Skybox")).left().padTop(10f).row()
        val imageChooserTable = VisTable()
        imageChooserTable.addSeparator().colspan(3).row()
        imageChooserTable.add(VisLabel("The 6 images must be square and of equal size")).colspan(3).row()
        imageChooserTable.padTop(6f).padRight(6f)
        imageChooserTable.add(positiveX)
        imageChooserTable.add(negativeX)
        imageChooserTable.add(positiveY).row()
        imageChooserTable.add(negativeY)
        imageChooserTable.add(positiveZ)
        imageChooserTable.add(negativeZ).row()
        root.add(imageChooserTable).left().top().row()

        // Create skybox buttons
        val createTable = VisTable()
        createTable.defaults().padTop(15f).padLeft(6f).padRight(6f)
        createTable.add(VisLabel("Skybox Name: ")).left()
        createTable.add(skyboxName).row()
        createTable.add(createBtn).colspan(2).center()
        root.add(createTable).row()

        // Options
        root.add(VisLabel("Options")).left().padTop(10f).row()
        root.addSeparator().row()

        val tab = VisTable()
        tab.defaults().padTop(15f).padLeft(6f).padRight(6f).padBottom(15f)
        tab.add(defaultBtn).expandX().fillX()
        tab.add(deletBtn).expandX().fillX().row()
        root.add(tab).fillX().expandX().row()
    }

    private fun setupListeners() {
        var projectContext = projectManager.current()

        // skybox select
        selectBox.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                projectContext.currScene.skybox?.dispose()

                projectManager.current().currScene.skybox = createSkyboxFromAsset(selectBox.selected, Shaders.skyboxShader)
                projectManager.current().currScene.skyboxAssetId = selectBox.selected.id
                resetImages()
            }
        })

        // create btn
        createBtn.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                //TODO validations on name input and if .sky file already exists
                // probably via listener on the name text field

                val oldSkybox = projectContext.currScene.skybox
                oldSkybox?.dispose()

                // Set actual skybox
                projectManager.current().currScene.skybox = Skybox(positiveX.file, negativeX.file,
                        positiveY.file, negativeY.file, positiveZ.file, negativeZ.file, Shaders.skyboxShader)

                val files = ArrayList<FileHandle>()
                files.add(positiveX.file)
                files.add(negativeX.file)
                files.add(positiveY.file)
                files.add(negativeY.file)
                files.add(positiveZ.file)
                files.add(negativeZ.file)

                val textureAssets = ArrayList<String>()

                // Create texture assets for each skybox image
                for (file in files) {
                    var asset: Asset

                    try {
                        asset = projectManager.current().assetManager.createTextureAsset(file)
                    } catch (e: AssetAlreadyExistsException) {
                        Mundus.postEvent(LogEvent(LogType.ERROR, "Skybox texture already exists. " + file.name()))
                        asset = projectManager.current().assetManager.findAssetByFileName(file.name())
                    }

                    textureAssets.add(asset.id)
                }

                // Create the skybox asset
                val skyboxAsset = projectManager.current().assetManager.createSkyBoxAsset(skyboxName.text, textureAssets[0], textureAssets[1],
                        textureAssets[2], textureAssets[3], textureAssets[4], textureAssets[5])

                projectManager.current().currScene.skyboxAssetId = skyboxAsset.id
                resetImages()
                refreshSelectBox()
            }
        })

        // default skybox btn
        defaultBtn.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                projectContext = projectManager.current()
                projectContext.currScene.skybox?.dispose()

                projectContext.currScene.skybox = createDefaultSkybox(Shaders.skyboxShader)
                val defaultSkybox = projectManager.getDefaultSkyboxAsset(projectContext, true)

                projectManager.current().currScene.skyboxAssetId = defaultSkybox.id
                refreshSelectBox()
                resetImages()
            }
        })

        // delete skybox btn
        deletBtn.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                projectContext = projectManager.current()
                projectContext.currScene.skybox?.dispose()

                projectContext.currScene.skybox = null
                projectContext.currScene.skyboxAssetId = null
                resetImages()
            }
        })

    }

    override fun show(stage: Stage?): VisDialog {
        // Update select box on dialog show
        refreshSelectBox()

        return super.show(stage)
    }

    private fun refreshSelectBox() {
        val skyboxes = projectManager.current().assetManager.getSkyboxAssets()
        val currentId = projectManager.current().currScene.skyboxAssetId

        // Refresh skyboxes
        selectBox.items.clear()
        selectBox.items = skyboxes
        selectBox.selected = projectManager.current().assetManager.findAssetByID(currentId) as SkyboxAsset?
    }

    private fun resetImages() {
        val skybox = projectManager.current().currScene.skybox
        if (skybox != null) {
            positiveX.setImage(skybox.positiveX)
            negativeX.setImage(skybox.negativeX)
            positiveY.setImage(skybox.positiveY)
            negativeY.setImage(skybox.negativeY)
            positiveZ.setImage(skybox.positiveZ)
            negativeZ.setImage(skybox.negativeZ)
        } else {
            positiveX.setImage(null)
            negativeX.setImage(null)
            positiveY.setImage(null)
            negativeY.setImage(null)
            positiveZ.setImage(null)
            negativeZ.setImage(null)
        }
    }

    override fun onProjectChanged(event: ProjectChangedEvent) {
        resetImages()
    }

    override fun onSceneChanged(event: SceneChangedEvent) {
        resetImages()
    }

}
