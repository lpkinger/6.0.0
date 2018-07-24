
///////////////////////////////////////////////////////////
// 1.Ext.data.JsonReader
///////////////////////////////////////////////////////////

Ext.override(Ext.data.JsonReader, {
    getJsonAccessor: function(){
        var re = /[\[\.]/;
        return function(expr) {
            try {
                // from here
/*
                return(re.test(expr))
                    ? new Function("rec", "defaultValue", "try{with (rec) return " + expr +
                        " || defaultValue;}catch(e){return defaultValue;}")
                    : function(rec, defaultValue){
                        try {
                            return rec[expr] || defaultValue;
                        } catch (e) {
                            return defaultValue;
                        }
                    };
*/
                // to here
                return(re.test(expr))
                    ? new Function("rec", "defaultValue", "try{with (rec) if (" + expr + " !== 0) {return " + expr +
                        " || defaultValue;} else {return 0;}}catch(e){return defaultValue;}")
                    : function(rec, defaultValue){
                        try {
                            if (rec[expr] !== 0) {
                                return rec[expr] || defaultValue;
                            } else {
                                return 0;
                            }
                        } catch (e) {
                            return defaultValue;
                        }
                    };
            } catch(e){}
            return Ext.emptyFn;
        };
    }(),

    /**
     * Create a data block containing Ext.data.Records from an XML document.
     * @param {Object} o An object which contains an Array of row objects in the property specified
     * in the config as 'root, and optionally a property, specified in the config as 'totalProperty'
     * which contains the total size of the dataset.
     * @return {Object} data A data block which is used by an Ext.data.Store object as
     * a cache of Ext.data.Records.
     */
    readRecords : function(o){
        /**
         * After any data loads, the raw JSON data is available for further custom processing.
         * @type Object
         */
        this.jsonData = o;
        var s = this.meta, Record = this.recordType,
            f = Record.prototype.fields, fi = f.items, fl = f.length;

//      Generate extraction functions for the totalProperty, the root, the id, and for each field
        if (!this.ef) {
            if(s.totalProperty) {
                this.getTotal = this.getJsonAccessor(s.totalProperty);
            }
            if(s.successProperty) {
                this.getSuccess = this.getJsonAccessor(s.successProperty);
            }
            this.getRoot = s.root ? this.getJsonAccessor(s.root) : function(p){return p;};
            if (s.id) {
                var g = this.getJsonAccessor(s.id);
                this.getId = function(rec) {
                    var r = g(rec);
                    return (r === undefined || r === "") ? null : r;
                };
            } else {
                this.getId = function(){return null;};
            }
            this.ef = [];
            for(var i = 0; i < fl; i++){
                f = fi[i];
                var map = (f.mapping !== undefined && f.mapping !== null) ? f.mapping : f.name;
                this.ef[i] = this.getJsonAccessor(map);
            }
        }

        var root = this.getRoot(o), c = root.length, totalRecords = c, success = true;
        if(s.totalProperty){
            var v = parseInt(this.getTotal(o), 10);
            if(!isNaN(v)){
                totalRecords = v;
            }
        }
        if(s.successProperty){
            var v = this.getSuccess(o);
            if(v === false || v === 'false'){
                success = false;
            }
        }
        var records = [];
        for(var i = 0; i < c; i++){
            var n = root[i];
            var values = {};
            var id = this.getId(n);
            for(var j = 0; j < fl; j++){
                f = fi[j];
                // from here
                var v = this.ef[j](n, f.defaultValue);
                values[f.name] = f.convert(v);
                // to here
            }
            var record = new Record(values, id);
            record.json = n;
            records[i] = record;
        }
        return {
            success : success,
            records : records,
            totalRecords : totalRecords
        };
    }
});

///////////////////////////////////////////////////////////
// 2.Ext.menu.Adapter Ext.Toolbar.MenuButton
///////////////////////////////////////////////////////////

if (Ext.version == '3.0') {
    Ext.menu.Adapter = Ext.menu.Menu;
    Ext.Toolbar.MenuButton = Ext.SplitButton;
}

///////////////////////////////////////////////////////////
// 3.Ext.Container
///////////////////////////////////////////////////////////

if (typeof Ext.Container.prototype.removeAll == 'undefined') {
    Ext.override(Ext.Container, {
        removeAll: function() {
            var item = null;
            while ((item = this.items.last())) {
                this.remove(item, true);
            }
        }
    });
}

///////////////////////////////////////////////////////////
// 4.Ext.TreeField
///////////////////////////////////////////////////////////
Ext.override(Ext.menu.DateMenu,{
    render : function(){
        Ext.menu.DateMenu.superclass.render.call(this);
        if(Ext.isGecko){
            this.picker.el.dom.childNodes[0].style.width = '178px';
            this.picker.el.dom.style.width = '178px';
        }
    }
});
