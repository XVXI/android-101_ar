package com.transcode.smartcity101p2.model

class YoutubePlayListResponse {

    var kind: String? = null
    var etag: String? = null
    var nextPageToken: String? = null
    var pageInfo: PageInfo? = null
    var items: ArrayList<PlayListItem>? = null

    class PageInfo {
        var totalResults: String? = null
        var resultsPerPage: String? = null
    }

    class PlayListItem {
        var kind: String? = null
        var etag: String? = null
        var id: String? = null
        var snippet: Snippet? = null
    }

    class Snippet {
        var publishedAt: String? = null
        var channelId: String? = null
        var title: String? = null
        var description: String? = null
        var thumbnails: Thumbnails? = null
        var channelTitle: String? = null
        var playlistId: String? = null
        var position: String? = null
        var resourceId: ResourceId? = null
    }

    class Thumbnails {
        var default: ThumbnailDefault? = null
        var medium: ThumbnailMedium? = null
        var high: ThumbnailHigh? = null
        var standard: ThumbnailStandard? = null
        var maxres: ThumbnailMaxres? = null
    }

    class ThumbnailDefault {
        var url: String? = null
        var width: String? = null
        var height: String? = null
    }

    class ThumbnailMedium {
        var url: String? = null
        var width: String? = null
        var height: String? = null
    }

    class ThumbnailHigh {
        var url: String? = null
        var width: String? = null
        var height: String? = null
    }

    class ThumbnailStandard {
        var url: String? = null
        var width: String? = null
        var height: String? = null
    }

    class ThumbnailMaxres {
        var url: String? = null
        var width: String? = null
        var height: String? = null
    }

    class ResourceId {
        var kind: String? = null
        var videoId: String? = null
    }
}