//
//  ViewController.swift
//  ZCRMSwiftAppTemplate
//

import UIKit

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
        print("Logout button pressed!")
//        LoginActivity().logout()
//        LoginActivity().loadZCRMLoginView()
        URLCache.shared.removeAllCachedResponses()
        
        if let cookies = HTTPCookieStorage.shared.cookies {
            for cookie in cookies {
                HTTPCookieStorage.shared.deleteCookie(cookie)
            }
        }
    }
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        if let path = Bundle.main.path(forResource : "AppConfiguration", ofType: "plist" )
        {
            let plist =  NSMutableDictionary( contentsOfFile : path )
            if( plist?["FirstLaunch"]! as? Bool == false )
            {
                //LoginActivity().loadZCRMLoginView()
                plist?.setValue( true, forKey : "FirstLaunch" )
                plist?.write( toFile : path, atomically : false )
            }
        }
    }
    
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func getAllContacts()
    {
//        let module : ZCRMModule = ZCRMModule(moduleAPIName: "Contacts")
//        let contacts : [ ZCRMRecord ] = try! module.getRecords().getData() as! [ZCRMRecord]
//        
//        if ( list.isEmpty == false )
//        {
//            list.removeAll()
//        }
//        
//        for index in 0..<contacts.count - 1
//        {
//            let lastName = try! contacts[index].getValue(ofField: "Last_Name") as! String
//            list.append( lastName )
//        }
    }
    
}

