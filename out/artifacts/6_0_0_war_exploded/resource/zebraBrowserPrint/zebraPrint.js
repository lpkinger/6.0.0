var selected_printer = null;
var default_printer = null;
var available_printers = null;
var resolutionCookie = getCookie("resolution");
function setup_web_print(callback){
	selected_printer = null;
	default_printer = null;
	available_printers = null;
	var combo_printers = new Array();
	BrowserPrint.getDefaultDevice('printer', function(printer) {
         default_printer = printer;
		if ((printer != null) && (printer.connection != undefined)) {
			selected_printer = printer;
		}
		BrowserPrint.getLocalDevices(function(printers) {
			available_printers = printers;
			var printers_available = false;
			if (printers != undefined) {
				for (var i = 0; i < printers.length; i++) {
						var print = new Object();
						print.display = printers[i].uid;
						print.value = printers[i].uid;
						combo_printers.push(print);
						printers_available = true;
					}
			}
			if (!printers_available) {
				showError("No Zebra Printers could be found!");
				return;
			} else if (selected_printer == null) {
				onPrinterSelected();
			}
			callback.call(null,combo_printers,selected_printer);
		}, undefined, 'printer');
	}, function(error_response) {
		showBrowserPrintNotFound();
	})
};

function sendData(caller,printer,dpi,params) {
	for (var i = 0; i < available_printers.length; i++) {
		if(available_printers[i].uid = printer){
			selected_printer = available_printers[i];
		}
	}
	checkPrinterStatus(function(text) {
		if (text == "Ready to Print") {
			if (dpi != resolutionCookie) {
				SetCookie("resolution", dpi);
			}
			Ext.Ajax.request({
	    		url : basePath +'api/pda/print/zplPrint.action',
				params: {
					caller:caller,
					dpi:dpi,
					data: unescape(escape(Ext.JSON.encode(params)))
				},
				method : 'post',
				timeout: 60000,
				callback : function(options,success,response){
					var res = new Ext.decode(response.responseText);
					if(res.success){
						selected_printer.send(res.data);
					}else if(res.exceptionInfo){
						var str = res.exceptionInfo;
						showError(str);return;
					}
				}
    	    });
		} else {
			printerError(text);
		}
	});
};

function checkPrinterStatus(finishedFunction) {
	selected_printer.sendThenRead("~HQES", function(fn) {
		var that = this;
		var statuses = new Array();
		statuses.push("Ready to Print");
		/*finishedFunction(statuses.join());*/
		fn(statuses.join());
		
		/*		
		var statuses = new Array();
		var ok = false;
		var is_error = text.charAt(70);
		var media = text.charAt(88);
		var head = text.charAt(87);
		var pause = text.charAt(84);
		// check each flag that prevents printing
		if (is_error == '0') {
			ok = true;
			statuses.push("Ready to Print");
		}
		if (media == '1')
			statuses.push("Paper out");
		if (media == '2')
			statuses.push("Ribbon Out");
		if (media == '4')
			statuses.push("Media Door Open");
		if (media == '8')
			statuses.push("Cutter Fault");
		if (head == '1')
			statuses.push("Printhead Overheating");
		if (head == '2')
			statuses.push("Motor Overheating");
		if (head == '4')
			statuses.push("Printhead Fault");
		if (head == '8')
			statuses.push("Incorrect Printhead");
		if (pause == '1')
			statuses.push("Printer Paused");
		if ((!ok) && (statuses.Count == 0))
			statuses.push("Error: Unknown Error");
		finishedFunction(statuses.join());*/
	}(finishedFunction), printerError);
};

function onPrinterSelected() {
	selected_printer = available_printers[0];
};

function trySetupAgain() {
	setup_web_print();
};

function showBrowserPrintNotFound() {
	showError("请检查打印机连接情况.");

};
function printerError(text) {
	showError("请检查打印机连接情况.");
};