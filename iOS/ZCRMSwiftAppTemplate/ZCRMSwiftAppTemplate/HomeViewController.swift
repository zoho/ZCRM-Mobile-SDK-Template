//
//  HomeViewController.swift
//  ZCRMSwiftAppTemplate
//

import UIKit
import ZCRMiOS

class HomeViewController: UIViewController {

	@IBOutlet weak var profileImage: UIImageView!
	@IBOutlet weak var userTextView: UITextView!
	@IBOutlet weak var contactsBtn: UIView!
	@IBOutlet weak var tasksBtn: UIView!
	@IBOutlet weak var contactsBtnText: UITextView!
	@IBOutlet weak var tasksBtnText: UITextView!
	
	private let restClient : ZCRMRestClient = ZCRMRestClient()
	private var records: [ZCRMRecord] = [ZCRMRecord]()
	private var module: String = ""
	
	override func viewDidLoad() {
        super.viewDidLoad()
		
		( UIApplication.shared.delegate as! AppDelegate ).loadLoginView { ( success ) in
			if( success == true )
			{
				print( "Login successful" )
				self.setOrganizationTitle()
				self.showUserImage()
			}
		}
		
		self.addLogoutButton()
		self.addGestures()
		self.addShadows()
		self.renderNavBar()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
	
	@objc private func logout(_ sender: Any) {
		
		( UIApplication.shared.delegate as! AppDelegate ).logout { (success) in
			
			if success {
				print("logout successfull")
			}
		}
	}
	
	private func showUserImage() {
		
		do {
			let currentUser: ZCRMUser = try self.restClient.getCurrentUser().getData() as! ZCRMUser
			let profilePicture: Data = try currentUser.downloadProfilePhoto().getFileData()
			self.profileImage.image = UIImage(data: profilePicture)
			self.userTextView.text = "Welcome, " + currentUser.getFullName()! + "!"
			self.profileImage.layer.cornerRadius = self.profileImage.frame.size.width / 2

		} catch {
			print(error)
		}
	}
	
	private func setOrganizationTitle() {
		
		
		do {
			let organization: ZCRMOrganisation = try self.restClient.getOrganisationDetails().getData() as! ZCRMOrganisation
			let title : String = organization.getCompanyName()
			self.navigationItem.title = title
		} catch {
			print(error)
		}
	}
	
	@objc private func showContacts(_ sender: Any) {
		
		self.getRecords(moduleApi: "Contacts")
	}
	
	@objc private func showTasks(_ sender: Any) {
		
		self.getRecords(moduleApi: "Tasks")
	}
	
	private func getRecords(moduleApi: String) {
		
		do {
			let module: ZCRMModule = ZCRMModule(moduleAPIName: moduleApi)
			self.records = try module.getRecords().getData() as! [ZCRMRecord]
			self.module = moduleApi
		} catch {
			print(error)
		}
		performSegue(withIdentifier: "listView", sender: self)
	}
	
	override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
		
		if segue.destination is ListViewController {
			
			let listViewController: ListViewController = (segue.destination as? ListViewController)!
			listViewController.entities = self.records
			listViewController.module = self.module
		}
		else {
			print(segue.destination)
		}
	}
	
	private func renderNavBar() {
		
		self.navigationController?.navigationBar.tintColor = .green
		let attrs = [NSAttributedStringKey.foregroundColor: UIColor.white, NSAttributedStringKey.font : UIFont.systemFont(ofSize: 22)]
		self.navigationController?.navigationBar.titleTextAttributes = attrs
		self.navigationController?.navigationBar.barTintColor = UIColor(red: 11/255, green: 94/255, blue: 122/255, alpha: 0)
		
	}
	
	
	private func addLogoutButton() {
		
		let button: UIButton = UIButton(frame: CGRect(x: 0, y: 0, width: 25, height: 25))
		button.setBackgroundImage(UIImage(named: "logoutIcon"), for: .normal)
		button.addTarget(self, action: #selector(self.logout(_:)), for: .touchUpInside)
		self.navigationItem.rightBarButtonItem = UIBarButtonItem(customView: button)
	}
	
	private func addGestures() {
		
		var tapGesture : UITapGestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(self.showContacts(_:)))
		self.contactsBtn.addGestureRecognizer(tapGesture);
		tapGesture = UITapGestureRecognizer(target: self, action: #selector(self.showTasks(_:)))
		self.tasksBtn.addGestureRecognizer(tapGesture)
	}

	private func addShadows() {
		
		self.contactsBtn.layer.borderColor = UIColor.gray.cgColor
		self.contactsBtn.layer.borderWidth = 0.6
		self.tasksBtn.layer.borderColor = UIColor.gray.cgColor
		self.tasksBtn.layer.borderWidth = 0.6
		self.contactsBtnText.font = UIFont.systemFont(ofSize: 16)
		self.tasksBtnText.font = UIFont.systemFont(ofSize: 16)
		self.userTextView.font = UIFont.systemFont(ofSize: 21)
		
	}
}
