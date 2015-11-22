package projecttracker

class EchoController {

	def index() {}
	
	def shout() {
		def message = params.value
		render "hello ${message}"
	}
}
