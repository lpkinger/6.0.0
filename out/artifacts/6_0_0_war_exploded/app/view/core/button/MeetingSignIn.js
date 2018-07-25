/**
 * 会议签到
 */	
Ext.define('erp.view.core.button.MeetingSignIn',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpMeetingSignInButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	id:'MeetingSignIn',
    	text: $I18N.common.button.erpMeetingSignInButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 110,
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		listeners: {
			click: function(m){
        		Ext.getCmp('MeetingSignIn').turn('Meeting!SignIn', 'md_maid=(select ma_id from Meetingroomapply where ma_code=\'' + Ext.getCmp('md_meetingcode').value + '\' )', 'oa/meeting/meetingSignIn.action');
        	}
		},
		turn: function(nCaller, condition, url){
	    	var win = new Ext.window.Window({
		    	id : 'win',
				    height: "100%",
				    width: "80%",
				    maximizable : true,
					buttonAlign : 'center',
					layout : 'anchor',
				    items: [{
				    	  tag : 'iframe',
				    	  frame : true,
				    	  anchor : '100% 100%',
				    	  layout : 'fit',
				    	  html : '<iframe id="iframe_' + caller + '" src="' + basePath + 'jsps/common/editorColumn.jsp?caller=' + nCaller 
				    	  	+ "&condition=" + condition +'" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
				    }],
				    buttons : [{
				    	name: 'confirm',
				    	text : $I18N.common.button.erpConfirmButton,
				    	iconCls: 'x-button-icon-confirm',
				    	cls: 'x-btn-gray',
				    	listeners: {
				    		buffer: 500,
				    		click: function(btn) {
				    			var grid = Ext.getCmp('win').items.items[0].body.dom.getElementsByTagName('iframe')[0].contentWindow.Ext.getCmp("editorColumnGridPanel");
	   				    		btn.setDisabled(true);
	   				    		grid.updateAction(url);
				    		}
				    	}
				    }, {
				    	text : $I18N.common.button.erpCloseButton,
				    	iconCls: 'x-button-icon-close',
				    	cls: 'x-btn-gray',
				    	handler : function(){
				    		Ext.getCmp('win').close();
				    	}
				    }]
				});
				win.show();
		}
	});