// Switch to the kitchensink database
use kitchensink;

// Clear existing data
db.members.deleteMany({});

// Insert seed data
db.members.insertOne({
    name: "John Smith",
    email: "john.smith@mailinator.com",
    phoneNumber: "2125551212"
});

// Insert additional test data
db.members.insertOne({
    name: "Jane Doe",
    email: "jane.doe@mailinator.com",
    phoneNumber: "2125551213"
});

db.members.insertOne({
    name: "Bob Wilson",
    email: "bob.wilson@mailinator.com",
    phoneNumber: "2125551214"
});

print("Seed data loaded successfully"); 