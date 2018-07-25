/**
 * 自定义grid.column
 * yn即yes/no,显示为是和否,其实际值对应-1和0
 */
Ext.define('erp.view.core.grid.YnColumn', {
    extend: 'Ext.grid.column.Column',
    alias: ['widget.yncolumn'],
    trueText: $I18N.common.form.yes,
    falseText: $I18N.common.form.no,
    constructor: function(cfg){
        this.callParent(arguments);
        if(!this.readOnly){
            this.editor = {
                	xtype: 'combo',
                	store: Ext.create('Ext.data.Store', {
                        fields: ['display', 'value'],
                        data : [
                            {"display": $I18N.common.form.yes, "value": -1},
                            {"display": $I18N.common.form.no, "value": 0}
                        ]
                    }),
                    editable: false,
                    displayField: 'display',
                    valueField: 'value',
            		queryMode: 'local',
            		value: '0',
            		hideTrigger: false,
            		listeners: {
            			scope: this,
            			'change': function(c){
            				if(c.rawValue != this.trueText && c.rawValue != this.falseText){
            					//实现grid单元格编辑模式下，不让用户编辑combo
            					if(contains(c.rawValue, this.falseText, true)){
                					c.setValue(this.falseText);
                				} else if(contains(c.rawValue, this.trueText, true)){
                					c.setValue(this.trueText);
                				} else {
                					c.setValue(this.falseText);
                				}
            				}
            			}
            		}
            };
        }
        var trueText = this.trueText,
            falseText = this.falseText;
        this.renderer = function(value,meta,record,rol,col,store,view){
            if(value === undefined){
                return falseText;
            }
            if(!value || value == 0){
                return '<span style="color:#888;">' + falseText + '</span>';
            }
            return trueText;
        };
    },
  /*  renderer: function(val){
    	var res = val;
    	switch(val){
	    	case 0:
	    		res = $I18N.common.form.no;break;
	    	case -1:
	    		res = $I18N.common.form.yes;break;
	    	case 1:
	    		res = $I18N.common.form.yes;break;
    	}
    	return res;
    }*/
});
Ext.data.Types.YN = {
		convert : function(v, data) {
			if(!v || v == ''){
				v = '0';
			}	
			return v;
		},
		sortType : function(v) {
			return v.Latitude;
		},
		type : 'yn'
};