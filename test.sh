#!/bin/bash
lein voom freshen
lein voom build-deps
lein immutant test
