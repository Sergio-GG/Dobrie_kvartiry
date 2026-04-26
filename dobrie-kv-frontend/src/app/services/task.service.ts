import { HttpClient, HttpParams } from "@angular/common/http";
import { PageResponse } from "../models/pageResponse.model";
import { Task } from "../models/task.model";
import { Injectable } from "@angular/core";

@Injectable({ providedIn: 'root' })
export class TaskService {
  private apiUrl = '/api/tasks';

  constructor(private http: HttpClient) {}

  getTasks(page: number = 0, size: number = 10, status?: string) {
    let params = new HttpParams().set('page', page).set('size', size);
    if (status) params = params.set('status', status);
    
    return this.http.get<PageResponse<Task>>(this.apiUrl, { params });
  }

  createTask(task: Partial<Task>) {
    return this.http.post<Task>(this.apiUrl, task);
  }

  updateTask(id: number, status: string){
    const url = `${this.apiUrl}/${id}/status`; 
    const body = { status: status }; 
    return this.http.patch<Task>(url, body);
  }

  getTaskById(id: number){
    const url = `${this.apiUrl}/${id}`;
    return this.http.get<Task>(url);
  }

  deleteTask(id: number) {
    const url = `${this.apiUrl}/${id}`;
    return this.http.delete<Task>(url);
  }
}