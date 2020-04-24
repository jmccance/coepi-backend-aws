terraform {
  required_version = ">= 0.12"
  required_providers {
    aws = ">= 2.56.0"
    template = "~> 2.1"
  }

  backend "s3" {
    key    = "prd/tfstate"
  }
}

provider "aws" {
  region = var.region
}
