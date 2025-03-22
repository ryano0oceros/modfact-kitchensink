#!/bin/bash

# Run the MongoDB seed script
mongosh < src/main/resources/mongodb/seed.js

echo "Database seeding completed" 