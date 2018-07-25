/**
 * 附件下载按钮
 **
 * 已修改为form加载后，自动查找附件
 */	
Ext.define('erp.view.core.button.DownLoad',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpDownloadButton',
		iconCls: 'x-button-icon-download',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpDownloadButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 100,
        menu: Ext.create('Ext.menu.Menu', {
            minWidth: 600,
            minHeight: 500,
            margin: '0 0 10 0',
            floating: true,
            items: []
        }),
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		listeners: {
			afterrender: function(btn){
				//btn.hide();
				var form = btn.ownerCt.ownerCt;
				var id = Ext.getCmp(form.keyField).value;
				if(id != null && id != 0){
					btn.download(id);
				}
				Ext.getCmp(btn.ownerCt.ownerCt.keyField).on('change', function(){
					var id = Ext.getCmp(form.keyField).value;
					if(id != null && id != 0){
						btn.menu.removeAll(true);
						btn.download(id);
					}
				});
			}
		},
		download: function(id){
			var attach = new Array();
			Ext.Ajax.request({//拿到grid的columns
	        	url : basePath + 'common/getFormAttachs.action',
	        	async: false,
	        	params: {
	        		caller: caller,
	        		keyvalue:  id
	        	},
	        	method : 'post',
	        	callback : function(options,success,response){
	        		var res = new Ext.decode(response.responseText);
	        		if(res.exception || res.exceptionInfo){
	        			showError(res.exceptionInfo);
	        			return;
	        		}
	        		attach = res.attach != null ? res.attach : [];
	        	}
	        });
			var items = this.menu;
			items.add({
				height: 20,
				width: 600,
				style: 'background:#CDBA96;',
				html: '<h3>附件:</h3>',
			});
			Ext.each(attach, function(){
				var path = this.fa_path;
				var name = '';
				if(contains(path, '\\', true)){
					name = path.substring(path.lastIndexOf('\\') + 1);
				} else {
					name = path.substring(path.lastIndexOf('/') + 1);
				}
				items.add({
					style: 'background:#C6E2FF;',
					html: '<img src="' + basePath + 'resource/images/mainpage/things.png" width=16 height=16/>' + 
					 '<span>文件:' + name + '<a href="' + basePath + "common/download.action?path=" + path + '">下载</a></span>',
				});
			});
			/*var win = new Ext.window.Window({
	    		title: '附件下载',
		    	id : 'win',
				height: "60%",
				width: "40%",
				items: items,
				buttonAlign: 'center',
				buttons: [{
					text: $I18N.common.button.erpCloseButton,
			    	iconCls: 'x-button-icon-close',
			    	cls: 'x-btn-gray',
			    	handler: function(){
			    		Ext.getCmp('win').close();
			    	}
				}]
	    	});
			win.show();*/
			if(this.menu.items.items.length == 1){
				items.add({
					html: '<img src="' + basePath + 'resource/images/mainpage/things.png" width=16 height=16/>' + 
					 '(无)'
				});
			}
			for(var i=0;i<10;i++){
				items.add({
					html: '' 
				});
			}
			//this.showMenu();
			//this.menu.getEl().slideOut('b', { duration: 3000 });
			//this.hideMenu();
			//this.itemsize = items.length;
			//this.ownerCt.ownerCt.add(items);
		}
	});