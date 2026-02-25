provider "google" {
  project     = "ppjg-236308"
  region      = "us-central1"
}
terraform {
  backend "gcs" {
    bucket = "terraform-state243"
    prefix = "terraform/state"
  }
}
data "google_compute_image" "image" {
  project = "ubuntu-os-cloud"
  family = "ubuntu-2404-lts-amd64"
}
resource "google_compute_instance" "address" {
  name = "centurion"
  machine_type = "e2-micro"
  zone = "us-central1-a"
  boot_disk {
    initialize_params {
      image = data.google_compute_image.image.most_recent
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

