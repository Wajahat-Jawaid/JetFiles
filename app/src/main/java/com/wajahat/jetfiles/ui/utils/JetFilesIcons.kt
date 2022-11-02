package com.wajahat.jetfiles.ui.utils

import androidx.compose.material.Icon
import androidx.compose.material.IconToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import com.wajahat.jetfiles.R

@Composable
fun StarButton(
    isFavorite: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val clickLabel = stringResource(if (isFavorite) R.string.unstar else R.string.star)

    IconToggleButton(
        checked = isFavorite,
        onCheckedChange = {
            onClick()
        },
        modifier = modifier.semantics {
            // Use a custom click label that accessibility services can communicate to the user.
            // We only want to override the label, not the actual action, so for the action we pass null.
            this.onClick(label = clickLabel, action = null)
        }
    ) {
        StarIcon(isFavorite)
    }
}

@Composable
fun StarIcon(
    isFavorite: Boolean
) {
    // If the file is starred, show filled icon, else show the bordered icon
    Icon(
        painter =
        if (isFavorite) painterResource(id = R.drawable.ic_star_filled)
        else painterResource(id = R.drawable.ic_star_border),
        tint = Color(246, 190, 0),
        contentDescription = stringResource(R.string.starred)
    )
}