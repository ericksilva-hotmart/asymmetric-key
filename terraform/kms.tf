resource "aws_kms_key" "asymmetric_key" {
  description             = "Asymmetric key for encryption"
  customer_master_key_spec = "RSA_2048"
  deletion_window_in_days = 10
  key_usage               = "ENCRYPT_DECRYPT"
  is_enabled               = true
}

resource "aws_kms_alias" "alias_asymmetric_key" {
  name          = "alias/lock-data-key-rsa"
  target_key_id = aws_kms_key.asymmetric_key.id
}