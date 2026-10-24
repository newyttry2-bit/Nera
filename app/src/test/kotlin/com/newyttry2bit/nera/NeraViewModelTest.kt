package com.newyttry2bit.nera

import com.newyttry2bit.nera.data.model.AppTab
import org.junit.Assert.assertEquals
import org.junit.Test

class NeraViewModelTest {

    @Test
    fun `default tab enum starts with home`() {
        assertEquals(AppTab.Home, AppTab.entries.first())
    }
}
