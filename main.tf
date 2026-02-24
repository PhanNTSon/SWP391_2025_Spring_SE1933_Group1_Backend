provider "google" {
  project     = "ppjg"
  region      = "us-central1"
}
terraform {
  backend "gcs" {
    bucket = "terraform-state243"
    prefix = "terraform/state"
  }
}
data "google_compute_image" "image" {
  family = "ubuntu-2404-lts-amd64"
}
resource "google_compute_instance" "address" {
  name = "Centurion"
  machine_type = "e2-micro"
  boot_disk {
    initialize_params {
      image = data.google_compute_image.image.self_link
      size = 10
      type = "pd-standard"
    }
  }
  network_interface {
    access_config {
      
    }
    network = "private"
    subnetwork = "private1"
  }
  metadata_startup_script = file("install.sh")
  
}
resource "google_compute_network" "VPC" {
  name = "private"
  auto_create_subnetworks = false
}
resource "google_compute_subnetwork" "Subnetwork" {
  name = "private1"
  ip_cidr_range = "10.0.0.0/24"
  region = "us-central1"
  network = "private"
}

