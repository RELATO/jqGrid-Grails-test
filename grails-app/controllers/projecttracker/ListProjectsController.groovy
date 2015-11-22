package projecttracker

class ListProjectsController {

    def index = { 
		redirect(action:current)
	}

	def current = {
		def allProjects = ListProjects.list()
		[allProjects:allProjects]
	}

	def overdue = {
		render "Order Valentines Day Package"
	}
}
