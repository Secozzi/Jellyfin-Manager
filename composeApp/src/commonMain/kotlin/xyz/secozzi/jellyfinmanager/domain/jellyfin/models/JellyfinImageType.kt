package xyz.secozzi.jellyfinmanager.domain.jellyfin.models

import org.jellyfin.sdk.model.api.ImageType

enum class JellyfinImageType(val imageType: ImageType, displayName: String) {
    Primary(ImageType.PRIMARY, "Primary"),
    Clearart(ImageType.ART, "Clearart"),
    Backdrop(ImageType.BACKDROP, "Backdrop"),
    Banner(ImageType.BANNER, "Banner"),
    Box(ImageType.BOX, "Box"),
    BoxRear(ImageType.BOX_REAR, "Box (rear)"),
    Disc(ImageType.DISC, "Disc"),
    Logo(ImageType.LOGO, "Logo"),
    Menu(ImageType.MENU, "Menu"),
    Thumb(ImageType.THUMB, "Thumb"),
}

fun ImageType.toJellyfinImageType(): JellyfinImageType? {
    return when (this) {
        ImageType.PRIMARY -> JellyfinImageType.Primary
        ImageType.ART -> JellyfinImageType.Clearart
        ImageType.BACKDROP -> JellyfinImageType.Backdrop
        ImageType.BANNER -> JellyfinImageType.Banner
        ImageType.BOX -> JellyfinImageType.Box
        ImageType.BOX_REAR -> JellyfinImageType.BoxRear
        ImageType.DISC -> JellyfinImageType.Disc
        ImageType.LOGO -> JellyfinImageType.Logo
        ImageType.MENU -> JellyfinImageType.Menu
        ImageType.THUMB -> JellyfinImageType.Thumb
        else -> null
    }
}
