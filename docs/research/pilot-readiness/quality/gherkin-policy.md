# ILP Gherkin Policy

## Protagonist

The protagonist of a scenario is an actor pursuing a domain goal.

The system is not the protagonist.

Preferred actors include:

- teacher
- student
- coordinator
- school leader
- researcher
- authorized family member
- administrator

## Language

Scenarios must describe observable domain behaviour.

Avoid subjective adjectives such as:

- correct
- appropriate
- successful
- clear
- efficient
- valid

unless an objective domain rule defines them.

## Design independence

Do not describe:

- buttons
- screens
- tabs
- forms
- colours
- modals
- URLs
- JSON
- REST
- HTTP status codes
- database tables
- Java classes
- React components

## Structure

Given describes domain state or preconditions.

When describes the meaningful action performed by the actor.

Then describes observable consequences.

## Independence

Each scenario must be independently executable.

A scenario must not depend on another scenario having run first.

## Coverage families

For each business capability, assess:

HAPPY
VALIDATION
EXCEPTIONAL
AUTHORIZATION
SECURITY

Not every capability necessarily needs every family, but any omitted
family must be justified.

## Traceability

Every mandatory Pilot-1 scenario must trace to:

Requirement or research purpose
→ Gherkin scenario
→ automated test
→ CI evidence
→ data/evidence produced when applicable.
