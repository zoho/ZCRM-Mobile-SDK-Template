//
//  ListViewController.swift
//   ZCRMSwiftAppTemplate
//

import UIKit
import ZCRMiOS

class ListViewController: UITableViewController {

	@IBOutlet var listView: UITableView!
	var entities: [ZCRMRecord] = [ZCRMRecord]()
	var module: String?
	
	override func viewDidLoad() {
		super.viewDidLoad()
		self.listView.dataSource = self
	}
}

extension ListViewController {
	
	override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
		return self.entities.count
	}
	
	override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
		
		let cell: UITableViewCell = self.tableView.dequeueReusableCell(withIdentifier: "cell")!
		let record: ZCRMRecord = self.entities[indexPath.row]
		do {
			if self.module == "Contacts" {
				cell.textLabel?.text = try record.getValue(ofField: "Last_Name") as? String
			} else if self.module == "Tasks" {
				cell.textLabel?.text = try record.getValue(ofField: "Subject") as? String
			}
		} catch {
			print(error)
		}
		return cell
	}
}
