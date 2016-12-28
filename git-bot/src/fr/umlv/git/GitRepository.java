package fr.umlv.git;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Package name: fr.umlv.git
 * Created by Quentin Béacco and Charles Dasilva Costa
 * Thaw Project M1 Informatique
 */
public class GitRepository {
    private final Repository repo;

    public GitRepository(Path gitFile) throws IOException{
        repo = new FileRepository(gitFile.toString());
    }

    public List<String> getCommits(String branch) throws IOException, GitAPIException{
        List<String> commits = new ArrayList<>();
        Git git = new Git(repo);
        Iterable<RevCommit> revCommits = git.log().add(repo.resolve(branch)).call();

        for(RevCommit revCommit : revCommits){
            commits.add(revCommit.getName());
        }

        return commits;

    }

    public static void main(String[] args) throws IOException, GitAPIException {
        GitRepository gitRepo = new GitRepository(Paths.get("/home/qbeacco/Documents/Universite/Java/Projet/thaw/.git"));

        System.out.println(gitRepo.getCommits("master"));
    }
}
