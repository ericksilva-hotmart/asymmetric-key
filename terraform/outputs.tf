output "kms_key_id" {
  value = aws_kms_key.asymmetric_key.id
}

output "kms_key_arn" {
  value = aws_kms_key.asymmetric_key.arn
}

output "kms_alias_name" {
  value = aws_kms_alias.alias_asymmetric_key.name
}
