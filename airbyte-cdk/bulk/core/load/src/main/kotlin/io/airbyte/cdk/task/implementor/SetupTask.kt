/*
 * Copyright (c) 2024 Airbyte, Inc., all rights reserved.
 */

package io.airbyte.cdk.task.implementor

import io.airbyte.cdk.task.DestinationTaskLauncher
import io.airbyte.cdk.task.ImplementorTask
import io.airbyte.cdk.task.SyncTask
import io.airbyte.cdk.write.DestinationWriter
import io.micronaut.context.annotation.Secondary
import jakarta.inject.Singleton

interface SetupTask : SyncTask, ImplementorTask

/**
 * Wraps @[DestinationWriter.setup] and starts the open stream tasks.
 *
 * TODO: This should call something like "TaskLauncher.setupComplete" and let it decide what to do
 * next.
 */
class DefaultSetupTask(
    private val destination: DestinationWriter,
    private val taskLauncher: DestinationTaskLauncher
) : SetupTask {
    override suspend fun execute() {
        destination.setup()
        taskLauncher.handleSetupComplete()
    }
}

interface SetupTaskFactory {
    fun make(taskLauncher: DestinationTaskLauncher): SetupTask
}

@Singleton
@Secondary
class DefaultSetupTaskFactory(
    private val destination: DestinationWriter,
) : SetupTaskFactory {
    override fun make(taskLauncher: DestinationTaskLauncher): SetupTask {
        return DefaultSetupTask(destination, taskLauncher)
    }
}