Ext.define('erp.view.hr.employee.HrOrgMapViewport', {
	extend: 'Ext.Viewport',
	layout: 'border',
	initComponent : function(){ 
    	var me = this;
    	me.items = [{
			xtype: 'panel',
			region: 'center',
			layout: 'fit',
			id: 'drawpanel',
			items: [{
	            xtype: 'basedraw',
	            id: 'draw',
	        }],
	        tbar: [{
	        	xtype: 'label',
        		text: '横向显示层级:',
	        },{
        		xtype: 'numberfield',
        		id: 'xLevel',
        		minValue: 1,
        		enableKeyEvents: true,
        		listeners: {
        			keydown: function(el,key){
        				if(key.keyCode == 13){
        					var value = el.value;
                			var draw = Ext.getCmp('draw');
                			draw.removeAll();
                			me.updateXLevel(value);
                			me.createTextNode(value,me.data);
        				}
        			}
        		}
        	},{
        		xtype: 'button',
        		text: '确定',
        		style: {
        			backgroundColor: '#f3f3f3',
        			border: '1px solid #9d9d9d'
        		},
        		handler: function(btn){
        			var value = Ext.getCmp('xLevel').value;
        			var draw = Ext.getCmp('draw');
        			draw.removeAll();
        			me.updateXLevel(value);
        			me.createTextNode(value,me.data);
        		}
        	}]
		}];
    	
    	me.callParent(arguments); 
	},
	listeners: {
		afterrender: function(me){
			//获取设置的层级
			Ext.Ajax.request({
				url: basePath + 'hr/employee/getHrOrgMapLevel.action',
				success: function(response){
					var object = JSON.parse(response.responseText);
					Ext.getCmp('xLevel').setValue(object.level);
					var draw = Ext.getCmp('draw');
					draw.getEl().mask('loading');
					Ext.Ajax.request({
						url: basePath + 'hr/employee/getHrOrgMap.action',
						success: function(response){
							draw.getEl().unmask();
							var obj = JSON.parse(response.responseText);
							me.data = obj.tree[0];
							draw.horizontalDeep = object.level;
							draw.add(me.data);
					    }
					});
				}
			});
		}
	},
	createTextNode: function(xlevel,data) {
		var draw = Ext.getCmp('draw');
		draw.horizontalDeep = xlevel || 4;
		draw.add(data);
	},
	updateXLevel: function(xlevel){
		Ext.Ajax.request({
			url: basePath + 'hr/employee/updateHrOrgMapLevel.action',
			params: {
				level: xlevel
			},
			success: function(response){
				
		    }
		});
	}
});