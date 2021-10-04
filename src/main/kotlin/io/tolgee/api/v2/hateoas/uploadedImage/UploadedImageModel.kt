package io.tolgee.api.v2.hateoas.uploadedImage

import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.core.Relation
import java.util.*

@Suppress("unused")
@Relation(collectionRelation = "uploadedImages", itemRelation = "uploadedImage")
open class UploadedImageModel(
  val id: Long,
  val filename: String,
  val requestFilename: String,
  val createdAt: Date
) : RepresentationModel<UploadedImageModel>()