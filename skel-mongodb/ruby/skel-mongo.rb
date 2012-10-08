require 'rubygems'
require 'sinatra/base'
#require 'sinatra/reloader'
require 'mongo'
require 'json'

class MongoSkel < Sinatra::Base
 # register Sinatra::Reloader
   use Rack::Logger
  
   SERVICES = JSON.parse(ENV['VCAP_SERVICES'])
   
   host =     SERVICES['mongodb-2.0'].first['credentials']['hostname'] rescue 'localhost' 
   port =     SERVICES['mongodb-2.0'].first['credentials']['port'] rescue 27017 
   database = SERVICES['mongodb-2.0'].first['credentials']['db'] rescue 'db'
   username = SERVICES['mongodb-2.0'].first['credentials']['username'] rescue nil
   password = SERVICES['mongodb-2.0'].first['credentials']['password'] rescue nil
  
   DB = Mongo::Connection.new(host, port).db(database, :pool_size => 5,  :timeout => 5) 
  
  if username and password
    DB.authenticate(username, password)
  end
 
  configure do
    set(:port, ENV["VCAP_APP_PORT"] || 4567)
  end
  
  helpers do
    def logger
      request.logger
    end
  
    def db
      DB
    end 
  
    def collection!      
      if collection.count == 0         
        halt 404, "Collection #{params[:collection_name]} not found"
      end
      collection
    end
  
    def collection
      collection_name = params[:collection_name]
      db[collection_name]
    end
  
    def entity!
      result = collection.find_one("_id" => id)
      halt_on_empty! result, 404, "Entity not found"  
    end
  
    def id
      BSON::ObjectId(params[:_id])
    end
    
    def halt_on_empty!(obj, status, message)
      if obj.nil?        
        halt status, message
      else
        obj
      end
    end
    
    def doc!
      begin
        doc = JSON.parse(request.body.read)      
      rescue
        halt 400, "Wrong doc format, it should be compatible with JSON"
      else
        doc
      end
    end
  end
  
  get '/' do  
    "Datanet mapper (mongodb), currently we have following collections #{db.collection_names}"  
  end

  # operations on collection
  get '/:collection_name' do
    ids = []
    collection!.find.to_a.each { |e|
      ids << e["_id"].to_s                            
    }
    ids.to_json
  end

  delete '/:collection_name' do
    logger.debug "Getting #{params[:collection_name]} collection"        
    collection!.drop
  end

  post '/:collection_name' do
    logger.debug "Addint new entity into #{params[:collection_name]} collection"       
    collection.insert(doc!)
  end
  
  # operations on entity
  get '/:collection_name/:_id' do
    logger.debug "Getting #{params[:collection_name]}/#{params[:_id]}"        
    hash = entity!.to_hash
    hash.delete "_id"
    hash.to_s
  end

  delete '/:collection_name/:_id' do    
    logger.debug "Deleting #{params[:collection_name]}/#{params[:_id]}"
    entity!
    collection!.remove("_id" => id)
  end

  post '/:collection_name/:_id' do    
    logger.debug "Updating entity #{params[:_id]} located in #{params[:collection_name]}"
    entity = entity!                    
    doc!.each{|k,v| entity[k] = v}
    collection!.save(entity)
    "Entity updated"
  end

  put '/:collection_name/:_id' do  
    logger.debug "Replacing entity #{params[:_id]} located in #{params[:collection_name]}"
    entity!    
    collection!.update({"_id" => id}, doc!)    
    "Entity replaced"
  end

   run! if __FILE__ == $0
end

#MongoSkel.run!
