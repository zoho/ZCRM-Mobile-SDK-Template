//
//  ViewController.swift
//  ___PROJECTNAME___
//

import UIKit
import ZCRMiOS

class ViewController: UIViewController {
    
    var list : [ String ] = [ String ]()
    
    @IBOutlet var getContactsButton: UIButton!
    @IBOutlet var logoutButton: UIButton!
    
    
    @IBAction func getButtonAction( _ sender : UIButton )
    {
        self.getAllContacts()
        let contactsViewController = self.storyboard?.instantiateViewController(withIdentifier: "ContactsView") as! ContactsViewController
        contactsViewController.nameList = list
        self.navigationController?.pushViewController( contactsViewController, animated: true )
    }
    
    @IBAction func logoutButtonAction(_ sender: UIButton)
    {
        ( UIApplication.shared.delegate as! AppDelegate ).logout()
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        ( UIApplication.shared.delegate as! AppDelegate ).loadLoginView { ( success ) in
            if( success == true )
            {
                print( "Login successful" )
            }
        }
        // Do any additional setup after loading the view, typically from a nib.
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func getAllContacts()
    {
        do
        {
            let module : ZCRMModule = ZCRMModule(moduleAPIName: "Contacts")
            let contacts : [ ZCRMRecord ] = try module.getRecords().getData() as! [ZCRMRecord]
            
            if ( list.isEmpty == false )
            {
                list.removeAll()
            }
            
            if( contacts.isEmpty == false )
            {
                for index in 0..<contacts.count - 1
                {
                    let lastName = try contacts[ index ].getValue( ofField : "Last_Name" ) as! String
                    list.append( lastName )
                }
            }
        }
        catch
        {
            print( "Error occured ViewController.getAllContacts(), Error : \( error )" )
        }
    }
    
}

