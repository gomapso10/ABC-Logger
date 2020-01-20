package kaist.iclab.abclogger.collector.media

import io.objectbox.annotation.Entity
import kaist.iclab.abclogger.Base

@Entity
data class MediaEntity(
        var mimeType: String = ""
) : Base()