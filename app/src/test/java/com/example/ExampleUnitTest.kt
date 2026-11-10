package com.example

import com.example.data.model.ContentRating
import com.example.data.model.ProfileType
import com.example.data.model.UserProfile
import com.example.data.repository.TvRepository
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun contentRatingHierarchy_isCorrect() {
        // G allows only G
        assertTrue(ContentRating.G.isAllowedUnder(ContentRating.G))
        assertFalse(ContentRating.PG.isAllowedUnder(ContentRating.G))
        assertFalse(ContentRating.TV_MA.isAllowedUnder(ContentRating.G))

        // PG allows G and PG
        assertTrue(ContentRating.G.isAllowedUnder(ContentRating.PG))
        assertTrue(ContentRating.PG.isAllowedUnder(ContentRating.PG))
        assertFalse(ContentRating.TV_14.isAllowedUnder(ContentRating.PG))

        // TV-MA allows all
        assertTrue(ContentRating.G.isAllowedUnder(ContentRating.TV_MA))
        assertTrue(ContentRating.TV_MA.isAllowedUnder(ContentRating.TV_MA))
    }

    @Test
    fun userProfile_screenTimeCalculations_areAccurate() {
        val kidProfile = UserProfile(
            id = "test_kid",
            name = "Test Kid",
            type = ProfileType.KIDS,
            avatarEmoji = "🧸",
            maxRating = ContentRating.G,
            timeLimitMinutes = 30,
            timeUsedSeconds = 1800 // exactly 30 mins
        )

        assertTrue(kidProfile.isTimeLimitExceeded)
        assertEquals(0L, kidProfile.remainingSeconds)

        val unlimitProfile = UserProfile(
            id = "test_parent",
            name = "Test Parent",
            type = ProfileType.PARENT,
            avatarEmoji = "👑",
            maxRating = ContentRating.TV_MA,
            timeLimitMinutes = 0,
            timeUsedSeconds = 5000
        )

        assertFalse(unlimitProfile.isTimeLimitExceeded)
        assertEquals(Long.MAX_VALUE, unlimitProfile.remainingSeconds)
    }

    @Test
    fun defaultTvApps_haveAppropriateRatings() {
        val apps = TvRepository.getDefaultApps()
        assertTrue(apps.isNotEmpty())

        val pbsKids = apps.find { it.id == "app_pbs_kids" }
        assertNotNull(pbsKids)
        assertEquals(ContentRating.G, pbsKids?.rating)

        val maxTv = apps.find { it.id == "app_max_tv" }
        assertNotNull(maxTv)
        assertEquals(ContentRating.TV_MA, maxTv?.rating)
    }
}
