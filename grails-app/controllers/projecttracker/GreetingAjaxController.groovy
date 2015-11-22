package projecttracker

class GreetingAjaxController {

    def index() {}

    def initialGreeting() {
        render "Hello this is a greeting box."
    }

    static int counter = 0
    def count() {
        counter ++
        render "The current number now is ${counter}"
    }

    def greetName() {
        render "Hello there ${params.name}"
    }
	
}
