Ext.define('erp.view.ma.jprocess.JNodeManSetField',{ 
	extend: 'Ext.form.FieldSet',
	id: 'JnodeMan',
    alias: 'widget.JNodeManSetField',
    autoScroll:true,
    collapsible: true,
    collapsed: true,
    title: '',
    style: 'background:#f1f1f1;',
    margin: '2 2 2 2',
    tfnumber: 0,
    requiers:['erp.view.ma.jprocess.MultiField'],
    initComponent: function() {
    	this.columnWidth = 1;//强制占一行
    	this.cls = '';
    	this.callParent(arguments);
    	this.setTitle('<img src="' + basePath + 'resource/images/icon/detail.png" width=20 height=20/>&nbsp;&nbsp;'+this.fieldLabel);
    },
    layout:'column',
    defaults:{
    	columnWidth:1/3
    },
    items: [],
    setValue: function(value){
    	this.value = value;
    },
    listeners : {
    	afterrender: function(f){
			var me = this;
			if(f.value != null && f.value.toString().trim() != ''){
				var text = f.value.split(';');
				me.tfnumber = text.length;
				console.log(text[0]);
				for(var i=1; i<=me.tfnumber; i++){
					console.log(text[i-1]);
                   me.add({
                	   xtype:'multifield',
                  	   name :'nodedealmancode',
                  	   secondname :'nodedealman',
                  	   fieldLabel:'节点'+i,
                	   value:text[i-1].split('#')[0],
                	   secondvalue:text[i-1].split('#')[1]
                   });
				}
			} else {
                 f.hide();
			}
//			me.expand(true);
		}
    },
	getSubmitData : function() {
		var  names = new Array();
		var c = this.query('multifield');
		Ext.each(c, function(){
			if(!Ext.isEmpty(this.value)) {
				names.push(this.value+"#"+this.secondvalue);
			}
		});
		return names.join(';');
	},
	addItem: function(count){
		var me = this;	this.removeAll();
		for(var i=1;i<parseInt(count)+1;i++){		
			this.add({
			   xtype:'multifield',
          	   name :'nodedealmancode',
          	   secondname :'nodedealman',
          	   fieldLabel:'节点'+i,
          	   listeners : {
					aftertrigger : function(t, r) {
						t.setValue(r.get('em_code'));
					}
			  }
			});
		}
		me.expand(true);
		
	}
});