rootProject.name = "yggraTitle"

include(
    "api",
    "impl_v1_8_8",
    "impl_v1_11",
    "bootstrap",
    "plugin-test"
)

project(":impl_v1_8_8").projectDir = file("implementation/impl_v1_8_8")
project(":impl_v1_11").projectDir = file("implementation/impl_v1_11")